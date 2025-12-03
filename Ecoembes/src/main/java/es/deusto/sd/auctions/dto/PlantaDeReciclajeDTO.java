package es.deusto.sd.auctions.dto;

public class PlantaDeReciclajeDTO {
    private int numero;


    public PlantaDeReciclajeDTO(int tipo){
        this.numero = tipo;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
}

