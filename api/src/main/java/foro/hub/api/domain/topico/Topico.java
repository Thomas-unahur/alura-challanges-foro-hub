package foro.hub.api.domain.topico;


import foro.hub.api.domain.curso.Curso;
import foro.hub.api.domain.usuarios.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Table(name = "topicos")
@Entity(name = "Topico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String mensaje;

    @Column(name = "fecha_creacion",nullable = false)
    private Date fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TopicStatus status;

    @Setter
    @ManyToOne
    @JoinColumn(name = "autor_id",nullable = false)
    private Usuario autor;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "curso_id",nullable = false)
    private Curso curso;

    @Column(name = "num_respuestas")
    private int numRespuestas;

    public Topico(DTORegistroTopico dtoRegistroTopico) {
        this.titulo = dtoRegistroTopico.titulo();
        this.mensaje = dtoRegistroTopico.mensaje();
        this.fechaCreacion = new Date();
        this.status = dtoRegistroTopico.status();
        this.curso = new Curso(
                dtoRegistroTopico.curso().nombre(),
                dtoRegistroTopico.curso().categoria());
    }

    public void actualizarDatos(DTOActualizarTopico dtoActualizarTopico){

        if(dtoActualizarTopico.mensaje()!=null){
            this.mensaje = dtoActualizarTopico.mensaje();
        }

        if(dtoActualizarTopico.titulo()!=null){
            this.titulo = dtoActualizarTopico.titulo();
        }

        if(dtoActualizarTopico.status()!=null){
            this.status = dtoActualizarTopico.status();
        }
    }

}
