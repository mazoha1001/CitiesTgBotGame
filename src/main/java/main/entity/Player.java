package main.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

@Entity
@Getter
@Setter
public class Player {
    @Id
    private String id;
    private HashSet<String> busyCities;
    private Integer score;
    private Integer highScore;
    private String startLetter;
}
