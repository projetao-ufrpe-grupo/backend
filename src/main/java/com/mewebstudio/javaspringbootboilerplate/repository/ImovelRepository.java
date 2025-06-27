package com.mewebstudio.javaspringbootboilerplate.repository;
import com.mewebstudio.javaspringbootboilerplate.entity.Imovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ImovelRepository extends JpaRepository<Imovel, UUID> {
}
