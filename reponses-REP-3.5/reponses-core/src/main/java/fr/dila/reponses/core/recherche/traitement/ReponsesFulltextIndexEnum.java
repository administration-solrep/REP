package fr.dila.reponses.core.recherche.traitement;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import fr.dila.reponses.api.recherche.Requete;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.FullTextUtil;


public enum ReponsesFulltextIndexEnum {
        TXT_QUESTION("txtQuestion","q."){
            public Boolean isNeededBy(final Requete requete){ 
                return requete.getDansTexteQuestion();
            }
         },
        TXT_REPONSE("txtReponse","r."){ 
             public Boolean isNeededBy(final Requete requete){ 
                 return requete.getDansTexteReponse(); 
             }
         },
        SENAT_TITRE("senatTitre","q."){ 
            public Boolean isNeededBy(final Requete requete){ 
                return requete.getDansTitre();
            }
        };

        private static final String ECM_FULLTEXT = STSchemaConstant.ECM_SCHEMA_PREFIX + ":fulltext_";
        
        private String prefix, name;
        
        private Boolean UFNXQLModeActivated;
        
        ReponsesFulltextIndexEnum(String name,String prefix){
            this.name = name;
            this.prefix = prefix;
            this.UFNXQLModeActivated = true;
        }
        public String getIndexFullName(){
            String usedPrefix = prefix;
            if (!this.UFNXQLModeActivated){
                usedPrefix = StringUtils.EMPTY;
            }
            return usedPrefix + ECM_FULLTEXT + name;
        }
        
        public void activateUFNXQL(){
            this.UFNXQLModeActivated = true;
        }
        
        public void desactivateUFNXQL(){
            this.UFNXQLModeActivated = false;
        }
        
        /**
         * Donne une recherche fulltext sur l'index (opérateur de stemming appliqué)
         * @param content
         * @return
         */
        public String getFullText(String content){
            return getIndexFullName() + " = \"" + FullTextUtil.parseContent(content) + "\"";
        }
        
        /**
         * Donne une recherche exacte sur l'index
         * @param content
         * @return
         */
        public String getRechercheExacte(String content){
            // On enlève les sauts de lignes, attention : cette ligne rend la recherche exacte erronée, mais l'enlever provoque des erreurs.
            content = FullTextUtil.cleanContent(content);
            // On protège le minus
            content = content.replaceAll("-", "{-}");
            // On double les simples quotes
            content = StringEscapeUtils.escapeSql(content);
            return getIndexFullName() + " = \"" + content + "\"";
        }
        
        public abstract Boolean isNeededBy(final Requete requete);
    };
