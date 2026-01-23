### 立体判定形状

#### 球

- zoneShapeType："sphere"
- dimension：取dimension.y作为半径
> allowBadShape：正值校正额外使用dimension.y绝对值作为dimension.x和dimension.z绝对值，维度含有奇数个负值则反转
```json
{
	"zoneShapeType": "sphere",
	"start": {
		"center": {
			"centerType": "lockPlayer",
			"playerId": 1,
			"selectStanding": true,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "10.0,10.0,10.0",
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
			"centerType": "relative",
			"previousZoneId": 0,
			"progress": 0.0,
			"relative": "0.0,5.0,0.0",
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 10,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"dimensionType": "fixed",
			"fixed": 0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
boolean isZoneInverted = Mth.sign(dimension.x) * Mth.sign(dimension.y) * Mth.sign(dimension.z) < 0;
return (xDist*xDist + yDist*yDist + zDist*zDist <= Math.abs(radiusSq)) != isZoneInverted;
```

#### 半球

#### 正方体

- zoneShapeType："cube"
- dimension：取dimension.y作为半边长
- allowBadShape：正值校正额外使用dimension.y绝对值作为dimension.x和dimension.z绝对值，其余同长方体
```json
{
	"zoneShapeType": "cube",
	"start": {
		"center": {
			"centerType": "lockPlayer",
			"playerId": 0,
			"selectStanding": true,
			"randomRange": 0.0,
			"playerCenterLerp": -0.2
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "10.0,10.0,10.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "lockPlayer",
			"playerId": 0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "relative",
			"previousZoneId": 0,
			"progress": 0.0,
			"relative": "0.0,9.99,0.0",
			"randomRange": 20.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 11,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"dimensionType": "previous",
			"previousZoneId": 11,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 360.0
		}
	},
	"allowBadShape": false
}
```

#### 长方体

- zoneShapeType："cuboid"
- allowBadShape：正值校正同2D长方形，维度含有奇数个负值则反转
```json
{
	"zoneShapeType": "cube",
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
			"fixed": "20.0,10.0,10.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "lockPlayer",
			"playerId": 0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "relative",
			"previousZoneId": 0,
			"progress": 0.0,
			"relative": "0.0,9.99,0.0",
			"randomRange": 20.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 11,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"dimensionType": "previous",
			"previousZoneId": 11,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 360.0
		}
	},
	"allowBadShape": false
}
```
```java
boolean invertX = dimension.x < 0;
boolean invertY = dimension.y < 0;
boolean invertZ = dimension.z < 0;
return (isWithinAbsX != invertX) && (isWithinAbsY != invertY) && (isWithinAbsZ != invertZ);
```

#### 椭球

- zoneShapeType："ellipsoid"
- allowBadShape:：同长方体
```json
{
	"zoneShapeType": "ellipsoid",
	"start": {
		"center": {
			"centerType": "relative",
			"previousZoneId": 0,
			"progress": 0.0,
			"relative": "0.000000,50.000000,0.000000",
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "15.000000,5.000000,8.000000",
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
			"previousZoneId": 13,
			"progress": 0.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 13,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 5400.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```

# English

### 3D shape

#### Sphere

- zoneShapeType: "sphere"
- dimension: take dimension.y as the radius
> allowBadShape: Positive value correction additionally uses the absolute value of dimension.y as the absolute values for dimension.x and dimension.z. If the dimension contains an odd number of negative values, the shape is inverted.
```json
{
	"zoneShapeType": "sphere",
	"start": {
		"center": {
			"centerType": "lockPlayer",
			"playerId": 1,
			"selectStanding": true,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "10.0,10.0,10.0",
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
			"centerType": "relative",
			"previousZoneId": 0,
			"progress": 0.0,
			"relative": "0.0,5.0,0.0",
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 10,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"dimensionType": "fixed",
			"fixed": 0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```
```java
boolean isZoneInverted = Mth.sign(dimension.x) * Mth.sign(dimension.y) * Mth.sign(dimension.z) < 0;
return (xDist*xDist + yDist*yDist + zDist*zDist <= Math.abs(radiusSq)) != isZoneInverted;
```

#### Hemisphere

#### Cube

- zoneShapeType: "cube"
- dimension: take dimension.y as the half side length
- allowBadShape: Positive correction additionally uses the absolute value of dimension.y as the absolute value of dimension.z and dimension.x. The others are same as cuboid.
```json
{
	"zoneShapeType": "cube",
	"start": {
		"center": {
			"centerType": "lockPlayer",
			"playerId": 0,
			"selectStanding": true,
			"randomRange": 0.0,
			"playerCenterLerp": -0.2
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "10.0,10.0,10.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "lockPlayer",
			"playerId": 0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "relative",
			"previousZoneId": 0,
			"progress": 0.0,
			"relative": "0.0,9.99,0.0",
			"randomRange": 20.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 11,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"dimensionType": "previous",
			"previousZoneId": 11,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 360.0
		}
	},
	"allowBadShape": false
}
```

#### Cuboid

- zoneShapeType: "cuboid"
- allowBadShape: Positive correction same as 2D rectangle. If the dimension contains an odd number of negative values, the shape is inverted.
```json
{
	"zoneShapeType": "cube",
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
			"fixed": "20.0,10.0,10.0",
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "lockPlayer",
			"playerId": 0,
			"randomRange": 0.0
		}
	},
	"end": {
		"center": {
			"centerType": "relative",
			"previousZoneId": 0,
			"progress": 0.0,
			"relative": "0.0,9.99,0.0",
			"randomRange": 20.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 11,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"dimensionType": "previous",
			"previousZoneId": 11,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 360.0
		}
	},
	"allowBadShape": false
}
```
```java
boolean invertX = dimension.x < 0;
boolean invertY = dimension.y < 0;
boolean invertZ = dimension.z < 0;
return (isWithinAbsX != invertX) && (isWithinAbsY != invertY) && (isWithinAbsZ != invertZ);
```

#### Ellipsoid

- zoneShapeType: "ellipsoid"
- allowBadShape: Same as cuboid
```json
{
	"zoneShapeType": "ellipsoid",
	"start": {
		"center": {
			"centerType": "relative",
			"previousZoneId": 0,
			"progress": 0.0,
			"relative": "0.000000,50.000000,0.000000",
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "fixed",
			"fixed": "15.000000,5.000000,8.000000",
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
			"previousZoneId": 13,
			"progress": 0.0,
			"randomRange": 0.0,
			"playerCenterLerp": 0.0
		},
		"dimension": {
			"dimensionType": "previous",
			"previousZoneId": 13,
			"progress": 0.0,
			"scale": 1.0,
			"randomRange": 0.0
		},
		"rotation": {
			"rotationType": "fixed",
			"fixed": 5400.0,
			"randomRange": 0.0
		}
	},
	"allowBadShape": false
}
```