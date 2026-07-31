package foro.hub.api.domain.topico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Long> {


    Optional<Topico> findByTituloAndMensaje(String titulo,String mensaje);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Topico t SET t.numRespuestas = t.numRespuestas +1 WHERE t.id = :topicoID")
    Integer incrementarNumeroDeRespuestas(@Param("topicoID") Long topicoID);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Topico t SET t.numRespuestas = t.numRespuestas -1 WHERE t.id = :topicoID")
    Integer decrementarNumeroDeRespuestas(@Param("topicoID") Long topicoID);
}
