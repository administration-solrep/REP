package fr.dila.reponses.web.client;

import java.io.Serializable;

/**
 * Couche de transport des infos pour la page de migration questions closes
 *
 */
public class TimbreDTO implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String currentGouvernementLabel;
    
    private String nextGouvernementLabel;
    
    private Long protocolarOrder;
    
    public TimbreDTO(String currentGouvernementLabel, String nextGouvernementLabel, Long protocolarOrder) {
        super();
        this.currentGouvernementLabel = currentGouvernementLabel;
        this.nextGouvernementLabel = nextGouvernementLabel;
        this.protocolarOrder = protocolarOrder;
    }

    /**
     * getter du label de l'ancien ministere
     * @return
     */
    public String getCurrentGouvernementLabel() {
        return currentGouvernementLabel;
    }

    /**
     * setter du label de l'ancien ministere
     * @param currentGouvernementLabel
     */
    public void setCurrentGouvernementLabel(String currentGouvernementLabel) {
        this.currentGouvernementLabel = currentGouvernementLabel;
    }

    /**
     * getter label du nouveau ministere 
     * @return
     */
    public String getNextGouvernementLabel() {
        return nextGouvernementLabel;
    }

    /**
     * setter du label du nouveau ministere
     * @param nextGouvernementLabel
     */
    public void setNextGouvernementLabel(String nextGouvernementLabel) {
        this.nextGouvernementLabel = nextGouvernementLabel;
    }

    /**
     * getter de l'ordre protocolaire
     * @return
     */
	public Long getProtocolarOrder() {
		return protocolarOrder;
	}

	/**
	 * setter de l'ordre protocolaire
	 * @param protocolarOrder
	 */
	public void setProtocolarOrder(Long protocolarOrder) {
		this.protocolarOrder = protocolarOrder;
	}
}
