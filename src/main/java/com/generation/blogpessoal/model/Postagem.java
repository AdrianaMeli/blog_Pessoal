package com.generation.blogpessoal.model;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Id;
import java.time.LocalDate;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.GeneratedValue;

@Entity
@Table(name = "tb_postagens")
public class Postagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O Atributo Titulo é obrigatorio e não pode ser vazio!")
    @Size(min = 5, max = 100, message = "O Atributo Título deve conter no minimo 5 e no maximo 100 caracteres! ")
    private String titulo;

    @NotNull(message = "O Atributo Texto é obrigatorio!")
    @Size(min = 10, max = 100, message = "O Atributo Título deve conter no minimo 10 e no maximo 100 caracteres! ")
    private String texto;

    @UpdateTimestamp
    private LocalDate data;

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String titulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String texto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDate data() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}





