[English](#English)

### 二维判定形状

#### 圆形

- zoneShapeType: "circle"
- dimension：取dimension.x作为半径
> allowBadShape: 正值校正额外使用dimension.x绝对值作为dimension.z绝对值，dimension.x * dimension.z < 0时反转
```json
{
	"zoneShapeType": "circle",
	"start": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 0,
			"progress": 1.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 0,
			"progress": 1.0,
			"scale": 1.414,
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 1,
			"progress": 0.0,
			"randomRange": 64.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 1,
			"progress": 0.0,
			"scale": 0.5,
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
double dimSq = dimension.x * dimension.x;  
boolean isZoneInverted = Mth.sign(dimension.x) * Mth.sign(dimension.z) < 0;
double xDist = center.x - checkPos.x;  
double zDist = center.z - checkPos.z;  
return (xDist * xDist + zDist * zDist) <= Math.abs(dimSq) != isZoneInverted;  
```

#### 方形

- zoneShapeType："square"
- dimension：取dimension.x作为半边长
> allowBadShape：正值校正额外使用dimension.x绝对值作为dimension.z绝对值，其余同矩形
```json
{
	"zoneShapeType": "square",
	"start": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-60.0,0.0",
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
			"dimension": {
			"dimensionType": "fixed",
			"fixed": "128.0,255.0,128.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-60.0,0.0",
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "128.0,255.0,128.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```

#### 矩形

- zoneShapeType："rectangle"
- dimension：取dimension.x，dimension.z作为x、z方向半边长
> allowBadShape：dimension.x和dimension.z各自反转一半区域，先判断dimension.x
```json
{
	"zoneShapeType": "rectangle",
	"start": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-30.0,0.0",
			"randomRange": 128.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "30.0,20.0,50.0",
			"randomRange": 10.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-50.0,0.0",
			"randomRange": 64.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "30.0,20.0,50.0",
			"randomRange": 20.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
boolean invertX = dimension.x < 0;
boolean invertZ = dimension.z < 0;
boolean isWithinAbsX = Math.abs(finalCheckX) <= Math.abs(dimension.x); 
boolean isWithinAbsZ = Math.abs(finalCheckZ) <= Math.abs(dimension.z);
return (isWithinAbsX != invertX)  
        && (isWithinAbsZ != invertZ);
```

#### 平顶正六边形

- zoneShapeType："hexagon"
- dimension：取dimension.x作为外接圆半径
> allowBadShape：正值校正额外使用dimension.x绝对值作为dimension.z绝对值，取dimensin.x判断是否反转
```json
{
	"zoneShapeType": "hexagon",
	"start": {
		"center": {
			"centerType": "relative",
			"previousZoneId": 0,
			"progress": 1.0,
			"relative": "0.0,0.0,0.0",
			"randomRange": 0.0,
			"playerCenterLerp": 0.2
		},
		"dimension": {
			"dimensionType": "relative",
			"previousZoneId": 0,
			"progress": 1.0,
			"scale": 1.0,
			"relative": "-100.0,-240.0,-100.0",
			"randomRange": 10.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "relative",
			"previousZoneId": 4,
			"progress": 0.0,
			"relative": "0.0,-10.0,0.0",
			"randomRange": 100.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "0.0,0.0,0.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
double rawRadius = dimension.x;
boolean isZoneInverted = rawRadius < 0;
if (effectiveRadius <= EPSILON) {
    return false;
}
if (distSq < effectiveApothem * effectiveApothem) { // 内接圆判断
    return !isZoneInverted;
}
if (distSq > effectiveRadius * effectiveRadius) { // 外接圆判断
    return isZoneInverted;  
}
// 立方体坐标系判定 (Cube Coordinates)
return (rq == 0 && rr == 0 && rs == 0) != isZoneInverted;
```

#### 尖顶正多边形

- zoneShapeType："polygon"
- dimension：取dimension.x作为外接圆半径
- segments：多边形边数，不小于3
> allowBadShape：正值校正额外使用dimension.x绝对值作为dimension.z绝对值，取dimensin.x判断是否反转
```json
{
	"zoneShapeType": "polygon",
	"start": {
		"center": {
			"centerType": "lockPlayer",
			"playerId": 0,
			"selectStanding": true,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "80.0,2.0,80.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-60.0,0.0",
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "15.0,4.0,15.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false,
	"segments": 5
}
```
```java
double rawRadius = dimension.x;
boolean isZoneInverted = rawRadius < 0;
int expectedCrossProductSign = isZoneInverted ? -1 : 1;
if (effectiveRadius <= EPSILON) {
    return false;
}
if (distSq < effectiveApothem * effectiveApothem) { // 内接圆判断
    return !isZoneInverted;
}
if (distSq > effectiveRadius * effectiveRadius) { // 外接圆判断
    return isZoneInverted;
} 
// 遍历多边形的每条边，检查点是否始终位于所有边的同一侧
// 如果顶点是逆时针排列，点应始终位于所有边的“左侧”（期望叉积 >= 0）
// 如果是反转区域，点应始终位于所有边的“右侧”（期望叉积 <= 0）
	if (crossProduct * expectedCrossProductSign < -EPSILON) {
        return false;
    }
}
return true;
```

#### 星形

- zoneShapeType："star"
- dimension：取dimension.x作为外接圆半径，取dimension.z作为内接圆半径
- segments：多边形边数，不小于2
> allowBadShape：在dimension进行正值校正的基础上使用dimension.x和dimension.z较大者作为外接圆半径，取dimensin.x判断是否反转
```json
{
	"zoneShapeType": "star",
	"start": {
		"center": {
			"centerType": "lockPlayer",
			"playerId": 0,
			"selectStanding": true,
			"randomRange": 10.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "20.0,5.0,10.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": -360.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-60.0,0.0",
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "15.0,4.0,15.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "relative",
			"previousZoneId": 7,
			"progress": 0.0,
			"scale": 1.0,
			"relative": 720.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false,
	"segments": 5
}
```
```java
double rawOuterRadius = dimension.x;
double rawInnerRadius = dimension.z;
if (effectiveOuterRadius <= EPSILON) {
	return false;
}
boolean isZoneInverted = rawOuterRadius < 0;
if (distSq > effectiveOuterRadius * effectiveOuterRadius + EPSILON) { // 外接圆判断
    return isZoneInverted;
}
if (distSq < effectiveInnerRadius * effectiveInnerRadius - EPSILON) { // 内接圆判断
    return !isZoneInverted;
}
// 卷绕数法判断点是否在星形内部
return (windingNumber == 0) == isZoneInverted;
```

#### 椭圆

- zoneShapeType："ellipse"
- dimension：取dimension.x作为半长轴，dimension.z作为半短轴
> allowBadShape：正值校正同矩形，dimension.x与dimension.z异号则反转
```json
{
	"zoneShapeType": "ellipse",
	"start": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 0,
			"progress": 0.0,
			"randomRange": 10.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "128.0,1.5,12.8",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": -360.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 0,
			"progress": 0.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 6,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"dimensionType": "relative",
			"previousZoneId": 6,
			"progress": 0.0,
			"scale": 1.0,
			"relative": 720,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
boolean isDimXNegative = dimension.x < 0;
boolean isDimZNegative = dimension.z < 0;
boolean isZoneInverted = isDimXNegative != isDimZNegative;
// 处理退化情况：点或线段
if (effectiveA <= EPSILON && effectiveB <= EPSILON) {
	return distFromCenterSq < EPSILON * EPSILON) != isZoneInverted;
} else if (effectiveA <= EPSILON) {
	return (Math.abs(x_rotated) < EPSILON) && (Math.abs(z_rotated) <= effectiveB) != isZoneInverted;
} else if (effectiveB <= EPSILON) { // 退化为X轴线段
	return (Math.abs(z_rotated) < EPSILON) && (Math.abs(x_rotated) <= effectiveA) != isZoneInverted;
if (distFromCenterSq <= minRadiusSq) { // 内接圆判断
    return !isZoneInverted;
}
if (distFromCenterSq >= maxRadiusSq) { // 外接圆判断
    return isZoneInverted;
}
return (result <= 1.0 + EPSILON) != isZoneInverted;
```

#### 十字形

- zoneShapeType："cross"
- dimension：取dimension.x作为外正方形半边长，取dimension.z作为内正方形半边长
> allowBadShape：在dimension进行正值校正的基础上使用dimension.x和dimension.z较大者作为外正方形半边长，取dimensin.x判断是否反转
```json
{
	"zoneShapeType": "cross",
	"start": {
		"center": {
			"centerType": "lockPlayer",
			"playerId": 0,
			"selectStanding": true,
			"randomRange": 0.0,
			"playerCenterLerp": 0.2
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "15.0,1.5,5.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 18,
			"progress": 0.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 18,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 30.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
double rawOuterHalfWidth = dimension.x;
double rawInnerHalfWidth = dimension.z;
boolean isZoneInverted = rawOuterHalfWidth < 0;
return ( // 横向矩形：X轴长（外半长），Z轴窄（内半长）
        (Math.abs(pX_rotated) <= effectiveOuterHalfWidth
                && Math.abs(pZ_rotated) <= effectiveInnerHalfWidth)
        // 纵向矩形：X轴窄（内半长），Z轴长（外半长）
        ||
                (Math.abs(pX_rotated) <= effectiveInnerHalfWidth
                && Math.abs(pZ_rotated) <= effectiveOuterHalfWidth)
) == !isZoneInverted;
```

#### 环形

- zoneShapeType："ring"
- dimension：取dimension.x作为外圆半径，取dimension.z作为内圆半径
> allowBadShape：在dimension进行正值校正的基础上使用dimension.x和dimension.z较大者作为外接圆半径，取dimensin.x判断是否反转
```json
{
	"zoneShapeType": "ring",
	"start": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 18,
			"progress": 0.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 18,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 19,
			"progress": 0.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "21.213203,1.5,24.213203",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
double outerDimSq = dimension.x * dimension.x;  
double innerDimSq = dimension.z * dimension.z;
boolean isZoneInverted = dimension.x < 0;
return ( // 在外圆环内  
        distSq < outerDimSq  
         // 在内圆环内  
        && distSq > innerDimSq  
) == !isZoneInverted;
```

#### 不规则多边形
暂未实现

# English

### 2D shape

#### Circle

- zoneShapeType: "circle"
- dimension: take dimension.x as the radius
> allowBadShape: Positive correction additionally uses the absolute value of dimension.x as the absolute value of dimension.z, Invert when dimension.x * dimension.z < 0
```json
{
	"zoneShapeType": "circle",
	"start": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 0,
			"progress": 1.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 0,
			"progress": 1.0,
			"scale": 1.414,
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 1,
			"progress": 0.0,
			"randomRange": 64.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 1,
			"progress": 0.0,
			"scale": 0.5,
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
double dimSq = dimension.x * dimension.x;  
boolean isZoneInverted = Mth.sign(dimension.x) * Mth.sign(dimension.z) < 0;
double xDist = center.x - checkPos.x;  
double zDist = center.z - checkPos.z;  
return (xDist * xDist + zDist * zDist) <= Math.abs(dimSq) != isZoneInverted;  
```

#### Square

- zoneShapeType: "square"
- dimension: take dimension.x as the half side length
> allowBadShape: Positive correction additionally uses the absolute value of dimension.x as the absolute value of dimension.z. The others are same as rectangle
```json
{
	"zoneShapeType": "square",
	"start": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-60.0,0.0",
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
			"dimension": {
			"dimensionType": "fixed",
			"fixed": "128.0,255.0,128.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-60.0,0.0",
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "128.0,255.0,128.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```

#### Rectangle

- zoneShapeType: "rectangle"
- dimension: take dimension.x, dimension.z as the half-length in the x and z directions
> allowBadShape: Half of the zones for dimension.x and dimension.z are inverted.  Start by checking dimension.x.
```json
{
	"zoneShapeType": "rectangle",
	"start": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-30.0,0.0",
			"randomRange": 128.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "30.0,20.0,50.0",
			"randomRange": 10.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-50.0,0.0",
			"randomRange": 64.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "30.0,20.0,50.0",
			"randomRange": 20.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
boolean invertX = dimension.x < 0;
boolean invertZ = dimension.z < 0;
boolean isWithinAbsX = Math.abs(finalCheckX) <= Math.abs(dimension.x); 
boolean isWithinAbsZ = Math.abs(finalCheckZ) <= Math.abs(dimension.z);
return (isWithinAbsX != invertX)  
        && (isWithinAbsZ != invertZ);
```

#### Flat top regular hexagon

- zoneShapeType: "hexagon"
- dimension: take dimension.x as the radius of the circumscribed circle
> allowBadShape: Positive correction additionally uses the absolute value of dimension.x as the absolute value of dimension.z. Use dimensin.x to decide whether to invert
```json
{
	"zoneShapeType": "hexagon",
	"start": {
		"center": {
			"centerType": "relative",
			"previousZoneId": 0,
			"progress": 1.0,
			"relative": "0.0,0.0,0.0",
			"randomRange": 0.0,
			"playerCenterLerp": 0.2
		},
		"dimension": {
			"dimensionType": "relative",
			"previousZoneId": 0,
			"progress": 1.0,
			"scale": 1.0,
			"relative": "-100.0,-240.0,-100.0",
			"randomRange": 10.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "relative",
			"previousZoneId": 4,
			"progress": 0.0,
			"relative": "0.0,-10.0,0.0",
			"randomRange": 100.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "0.0,0.0,0.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
double rawRadius = dimension.x;
boolean isZoneInverted = rawRadius < 0;
if (effectiveRadius <= EPSILON) {
    return false;
}
if (distSq < effectiveApothem * effectiveApothem) { // 内接圆判断
    return !isZoneInverted;
}
if (distSq > effectiveRadius * effectiveRadius) { // 外接圆判断
    return isZoneInverted;  
}
// 立方体坐标系判定 (Cube Coordinates)
return (rq == 0 && rr == 0 && rs == 0) != isZoneInverted;
```

#### Spike regular polygon

- zoneShapeType: "polygon"
- dimension: take dimension.x as the radius of the circumscribed circle
- segments: the number of polygon edges, no less than 3
> allowBadShape: Positive correction additionally uses the absolute value of dimension.x as the absolute value of dimension.z. Use dimensin.x to decide whether to invert
```json
{
	"zoneShapeType": "polygon",
	"start": {
		"center": {
			"centerType": "lockPlayer",
			"playerId": 0,
			"selectStanding": true,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "80.0,2.0,80.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-60.0,0.0",
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "15.0,4.0,15.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false,
	"segments": 5
}
```
```java
double rawRadius = dimension.x;
boolean isZoneInverted = rawRadius < 0;
int expectedCrossProductSign = isZoneInverted ? -1 : 1;
if (effectiveRadius <= EPSILON) {
    return false;
}
if (distSq < effectiveApothem * effectiveApothem) { // 内接圆判断
    return !isZoneInverted;
}
if (distSq > effectiveRadius * effectiveRadius) { // 外接圆判断
    return isZoneInverted;
} 
// 遍历多边形的每条边，检查点是否始终位于所有边的同一侧
// 如果顶点是逆时针排列，点应始终位于所有边的“左侧”（期望叉积 >= 0）
// 如果是反转区域，点应始终位于所有边的“右侧”（期望叉积 <= 0）
	if (crossProduct * expectedCrossProductSign < -EPSILON) {
        return false;
    }
}
return true;
```

#### Star

- zoneShapeType: "star"
- dimension: dimension.x is used as the radius of the circumscribed circle, and dimension.z is used as the radius of the inscribed circle
- segments: the number of polygon edges, must be no less than 2
> allowBadShape: Based on positive value correction in dimension, the larger of dimension.x and dimension.z is used as the radius of the circumscribed circle. Use dimensin.x to decide whether to invert
```json
{
	"zoneShapeType": "star",
	"start": {
		"center": {
			"centerType": "lockPlayer",
			"playerId": 0,
			"selectStanding": true,
			"randomRange": 10.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "20.0,5.0,10.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": -360.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "fixed",
			"fixed": "0.0,-60.0,0.0",
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "15.0,4.0,15.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "relative",
			"previousZoneId": 7,
			"progress": 0.0,
			"scale": 1.0,
			"relative": 720.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false,
	"segments": 5
}
```
```java
double rawOuterRadius = dimension.x;
double rawInnerRadius = dimension.z;
if (effectiveOuterRadius <= EPSILON) {
	return false;
}
boolean isZoneInverted = rawOuterRadius < 0;
if (distSq > effectiveOuterRadius * effectiveOuterRadius + EPSILON) { // 外接圆判断
    return isZoneInverted;
}
if (distSq < effectiveInnerRadius * effectiveInnerRadius - EPSILON) { // 内接圆判断
    return !isZoneInverted;
}
// 卷绕数法判断点是否在星形内部
return (windingNumber == 0) == isZoneInverted;
```

#### Ellipse

- zoneShapeType: "ellipse"
- dimension: dimension.x is used as the semi-major axis, and dimension.z is used as the semi-minor axis.
> allowBadShape: Positive value correction is the same as for rectangles; if dimension.x and dimension.z have different signs, the shape is inverted.
```json
{
	"zoneShapeType": "ellipse",
	"start": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 0,
			"progress": 0.0,
			"randomRange": 10.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "128.0,1.5,12.8",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": -360.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 0,
			"progress": 0.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 6,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"dimensionType": "relative",
			"previousZoneId": 6,
			"progress": 0.0,
			"scale": 1.0,
			"relative": 720,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
boolean isDimXNegative = dimension.x < 0;
boolean isDimZNegative = dimension.z < 0;
boolean isZoneInverted = isDimXNegative != isDimZNegative;
// 处理退化情况：点或线段
if (effectiveA <= EPSILON && effectiveB <= EPSILON) {
	return distFromCenterSq < EPSILON * EPSILON) != isZoneInverted;
} else if (effectiveA <= EPSILON) {
	return (Math.abs(x_rotated) < EPSILON) && (Math.abs(z_rotated) <= effectiveB) != isZoneInverted;
} else if (effectiveB <= EPSILON) { // 退化为X轴线段
	return (Math.abs(z_rotated) < EPSILON) && (Math.abs(x_rotated) <= effectiveA) != isZoneInverted;
if (distFromCenterSq <= minRadiusSq) { // 内接圆判断
    return !isZoneInverted;
}
if (distFromCenterSq >= maxRadiusSq) { // 外接圆判断
    return isZoneInverted;
}
return (result <= 1.0 + EPSILON) != isZoneInverted;
```

#### Cross

- zoneShapeType: "cross",
- dimension: take dimension.x as the outer half-width, and dimension.z as the inner half-width
> allowBadShape: Based on positive value correction in dimension, the larger of dimension.x and dimension.z is used as the radius of the circumscribed circle. Use dimensin.x to decide whether to invert
```json
{
	"zoneShapeType": "cross",
	"start": {
		"center": {
			"centerType": "lockPlayer",
			"playerId": 0,
			"selectStanding": true,
			"randomRange": 0.0,
			"playerCenterLerp": 0.2
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "15.0,1.5,5.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 18,
			"progress": 0.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 18,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 30.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
double rawOuterHalfWidth = dimension.x;
double rawInnerHalfWidth = dimension.z;
boolean isZoneInverted = rawOuterHalfWidth < 0;
return ( // 横向矩形：X轴长（外半长），Z轴窄（内半长）
        (Math.abs(pX_rotated) <= effectiveOuterHalfWidth
                && Math.abs(pZ_rotated) <= effectiveInnerHalfWidth)
        // 纵向矩形：X轴窄（内半长），Z轴长（外半长）
        ||
                (Math.abs(pX_rotated) <= effectiveInnerHalfWidth
                && Math.abs(pZ_rotated) <= effectiveOuterHalfWidth)
) == !isZoneInverted;
```

#### Ring

- zoneShapeType: "ring",
- dimension: take dimension.x as the outer radius, and dimension.z as the inner radius
> allowBadShape: Based on positive value correction in dimension, the larger of dimension.x and dimension.z is used as the radius of the circumscribed circle. Use dimensin.x to decide whether to invert
```json
{
	"zoneShapeType": "ring",
	"start": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 18,
			"progress": 0.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 18,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "previous",
			"previousZoneId": 19,
			"progress": 0.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "21.213203,1.5,24.213203",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 0.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
double outerDimSq = dimension.x * dimension.x;  
double innerDimSq = dimension.z * dimension.z;
boolean isZoneInverted = dimension.x < 0;
return ( // 在外圆环内  
        distSq < outerDimSq  
         // 在内圆环内  
        && distSq > innerDimSq  
) == !isZoneInverted;
```

#### Irregular polygon
Not implemented yet