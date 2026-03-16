package xiao.battleroyale.api.algorithm;

public interface IAlgorithmApi {

    IRectangleGrid rectangleGrid();

    IGoldenSpiral goldenSpiral();
    @Deprecated default IGoldenSpiral golderSpiral() { // typo, lol
        return goldenSpiral();
    }

    ICircleGrid circleGrid();
}
