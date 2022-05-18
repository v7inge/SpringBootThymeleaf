package ru.aconsultant.thymeleaf.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Util {

    public static <T> List<T> getAsList(Iterable<T> iterable) {
        if (iterable == null) return null;
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
