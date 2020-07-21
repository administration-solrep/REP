package fr.dila.reponses.core.recherche.traitement;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.recherche.RequeteTraitement;

/**
 * Une classe pour parser le champ de numéo de question de l'utilisateur.
 * On veut que celui-ci puissent entrer une chaine de la forme 422-450,580,650-896
 * et rechercher les questions entre les intervalles 422-450 et 650-896 et les questions ayant pour 
 * numéro 580.
 * 
 * @author jgomez
 *
 */
public class NumeroQuestionTraitement implements RequeteTraitement<Requete>{
    
    private String targetField, indexField;
    
    public NumeroQuestionTraitement(){
        targetField = "q.qu:numeroQuestion";
        indexField = "q." + STSchemaConstant.ECM_SCHEMA_PREFIX + ":fulltext_idQuestion";
    }
    
    @Override
    public void init(Requete requete) {
        return ;
    }
    
    @Override
    public void doBeforeQuery(Requete requete) {
      String numeroQuestion = requete.getChampRequeteSimple();
      requete.setClauseChampRequeteSimple((parse(numeroQuestion)));
    }
    
    @Override
    public void doAfterQuery(Requete requete) {
        return ;
    }
    
    public String parse(String numeroStr){
        if (numeroStr == null){
            return null;
        }
        numeroStr = StringUtils.remove(numeroStr," ");
        StringTokenizer st = new StringTokenizer(numeroStr,",;");
        List<String> results = new ArrayList<String>();
        int blocCount = 0;
        while (st.hasMoreTokens()) {
            blocCount++;
            String bloc = st.nextToken();
            if (bloc.contains("-")){
                String intervalle = parseIntervalle(bloc);
                results.add(String.format("(%s)",intervalle));
            }
            else{
                if (blocCount == 1){
                    String index = parseIndex(bloc);
                    results.add(String.format("(%s)",index));
                }
                else{
                    String numero = parseNumero(bloc);
                    results.add(String.format("(%s)",numero));
                }
            }
        }
        String resultStr = StringUtils.join(results," OR ");
        return resultStr;
    }

    private String parseIndex(String bloc) {
        String result = String.format("%s LIKE \'%s\'",indexField,bloc);
        return result;
    }

    private String parseIntervalle(String bloc) {
        String[] numeros = bloc.split("-");
        String result = String.format("%s BETWEEN %s AND %s",targetField,numeros[0],numeros[1]);
        return result;
    }
    
    private String parseNumero(String bloc) {
        String result = String.format("%s = %s",targetField,bloc);
        return result;
    }


}
