[English](#English)

# 分布算法

## 均匀分布
> 用于避免随机计算玩家出生点位时，有概率刷在附近的问题

一共需要以下信息完成计算：
- 实际中心坐标（计算后的原点偏移）
- 区域维度（圆半径/矩形半边长等，同区域配置）
- 模拟点位总数
- 是否允许点位在区域边缘
- 全局缩放比例（便利参数）

### 黄金螺旋分布

在圆形区域内，使用黄金角螺旋 (Golden Angle/Fibonacci Spiral) 计算分散点位：
- 该方法在圆盘区域内产生相对均匀的分布，点位密度从中心向外均匀递减

### 双圆心网格分布

在圆形区域内，使用双圆心网格计算分散点位：
- N为奇数，使用(0, 0)为圆心，点数为 $1+4k$
- N为偶数，使用($\frac{\sqrt{2}}{2}$, $\frac{\sqrt{2}}{2}$)为圆心，点数为 $4k$

可预计算点位，从而调用时只需 $O(n)$ 遍历

### 网格采样

在矩形区域内，使用网格抖动采样 (Jittered Grid Sampling) 计算分散点位的基础网格中心点：
- 指定矩形、总点数，自动均匀切割

# English

## Uniform Distribution
> Used to avoid the issue where players might spawn near each other when randomly calculating spawn points

The following information is required to complete the calculation:
- Actual center coordinates (calculated origin offset)
- Area dimensions (circle radius/rectangle half-side length, etc., as per area configuration)
- Total number of simulated points
- Whether points are allowed on the edge of the area
- Global scaling factor (convenience parameter)

### Golden Spiral Distribution

In circular areas, use the Golden Angle/Fibonacci Spiral to calculate dispersed points:
- This method produces a relatively uniform distribution within the disk area, with point density decreasing uniformly from the center outward.

### Dual-Center Grid Distribution

In circular areas, use a dual-center grid to calculate dispersed points:
- If is odd, use (0, 0) as the center; the number of points is $1+4k$.
- If is even, use ($\frac{\sqrt{2}}{2}$, $\frac{\sqrt{2}}{2}$) as the center; the number of points is $4k$.

Points can be pre-calculated, allowing for $O(n)$ traversal during invocation.

### Grid Sampling

In rectangular areas, use Jittered Grid Sampling to calculate the base grid center points for dispersed positions:
- Specify the rectangle and total number of points; the area is then automatically and uniformly partitioned.