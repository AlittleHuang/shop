package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.CarouselFigure;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface CarouselFigureRepository extends JpaRepository<CarouselFigure, Integer>,
        JpaSpecificationExecutor<CarouselFigure> {

    void deleteById(int id);

    List<CarouselFigure> findByDisplay(int display, Sort sort);

}
