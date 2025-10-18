package xiao.battleroyale.api.algorithm;

import java.util.List;

public interface IPreCalculate {

    void preCalculate(int n);

    void preCalculate(List<Integer> nList);

    void preCalculate(int startN, int endN);
}
