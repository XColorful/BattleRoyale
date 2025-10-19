package xiao.battleroyale.algorithm;

import xiao.battleroyale.api.algorithm.IAlgorithmApi;
import xiao.battleroyale.api.algorithm.ICircleGrid;
import xiao.battleroyale.api.algorithm.IGoldenSpiral;
import xiao.battleroyale.api.algorithm.IRectangleGrid;

public class AlgorithmFacade implements IAlgorithmApi {

    private static final AlgorithmFacade INSTANCE = new AlgorithmFacade();
    public static IAlgorithmApi get() {
        return INSTANCE;
    }
    private AlgorithmFacade() {}

    @Override
    public IRectangleGrid rectangleGrid() {
        return Distribution.RectangleGrid.get();
    }

    @Override
    public IGoldenSpiral golderSpiral() {
        return Distribution.GoldenSpiral.get();
    }

    @Override
    public ICircleGrid circleGrid() {
        return Distribution.CircleGrid.get();
    }
}
