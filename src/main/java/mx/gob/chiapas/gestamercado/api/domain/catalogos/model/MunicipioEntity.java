package mx.gob.chiapas.gestamercado.api.domain.catalogos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "municipio", schema = "gesta")
public class MunicipioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "clave_inegi", nullable = false, length = 5, unique = true)
    private String claveInegi;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado = "Chiapas";

    @Column(name = "zona_horaria", nullable = false, length = 40)
    private String zonaHoraria = "America/Mexico_City";

    @Column(name = "dias_max_antiguedad_cobro", nullable = false)
    private short diasMaxAntiguedadCobro = 15;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    public MunicipioEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClaveInegi() {
        return claveInegi;
    }

    public void setClaveInegi(String claveInegi) {
        this.claveInegi = claveInegi;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getZonaHoraria() {
        return zonaHoraria;
    }

    public void setZonaHoraria(String zonaHoraria) {
        this.zonaHoraria = zonaHoraria;
    }

    public short getDiasMaxAntiguedadCobro() {
        return diasMaxAntiguedadCobro;
    }

    public void setDiasMaxAntiguedadCobro(short diasMaxAntiguedadCobro) {
        this.diasMaxAntiguedadCobro = diasMaxAntiguedadCobro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
