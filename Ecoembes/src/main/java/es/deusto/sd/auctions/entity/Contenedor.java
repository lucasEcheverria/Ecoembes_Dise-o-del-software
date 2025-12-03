package es.deusto.sd.auctions.entity;

import jakarta.persistence.*;

import java.util.*;

@Entity
public class Contenedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Estado estado;


    public Contenedor(long id) {
        this.id = id;
    }

    public Contenedor(){}

    public long getId() {
        return id;
    }

    public Estado getEstado(){return estado;}

    public void setEstado(Estado e){this.estado = e;}

    @Override
    public String toString() {
        return "Contenedor{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Contenedor that = (Contenedor) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
