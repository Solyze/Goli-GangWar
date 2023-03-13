package io.github.solyze.goligangwar.utility;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class Prediction {

    private final String redGang;
    private final String blueGang;
    private List<UUID> blueList;
    private List<UUID> redList;
    private boolean open;

    public Prediction(String redGang, String blueGang) {
        this.redGang = redGang;
        this.blueGang = blueGang;
        this.blueList = new ArrayList<>();
        this.redList = new ArrayList<>();
        this.open = true;
    }

    public int getRedPredictions() {
        return redList.size();
    }

    public int getBluePredictions() {
        return blueList.size();
    }
}
