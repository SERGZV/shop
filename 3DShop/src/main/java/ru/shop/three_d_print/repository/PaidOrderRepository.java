package ru.shop.three_d_print.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shop.three_d_print.entities.PaidOrder;

@Repository
public interface PaidOrderRepository extends JpaRepository<PaidOrder, Long> {}
