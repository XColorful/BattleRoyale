# 粒子配置

## 单个配置

- id：粒子配置唯一id
- name：为该配置命名，可重复
- color：暂时没有功能
- detail：粒子参数
- parameter：粒子额外参数（可选）
```json
{
	"id": 1,
	"name": "Simple Particle",
	"color": "#FFFFFF",
	"detail": {
		粒子参数,
		parameter: {
			粒子可选参数
		}
	}
}
```

### 粒子参数

- particleType：粒子类型
- count：单次生成的数量
- initDelay：第一次生成粒子的延迟
- interval：每次重复生成的间隔
- repeat：总生成次数，至少生成1次
- offset：初始偏移位置
- offsetRange：随机偏移范围
- exactOffset：是否在服务端计算偏移
> 若启用且count较大，则会严重影响网络带宽
```json
"detail": {
	"particleType": "minecraft:explosion",
	"count": 1,
	"lifeTime": 200,
	"initDelay": 20,
	"interval": 20,
	"repeat": 10,
	"offset": "0.0,0.0,0.0",
	"offsetRange": "0.0,0.0,0.0",
	"exactOffset": false
}
```

### 粒子额外参数

参数对特定粒子有效

- speed：控制粒子初速度
- color：粒子颜色
- scale：粒子大小
- note：音符粒子类别
- nbt：自定义粒子NBT，包含"item"和"block_state"的解析
```json
"detail": {
	粒子参数,
	"parameter": {
		"speed": 0.1,
		"color": "#0000FF",
		"scale": 1.0,
		"note": 0,
		"nbt": "{}"
	}
}
```

# English

## Single particle config

- id: unique particle id
- name: name the config, can be repeated
- color: no function for now
- detail: particle parameter
- parameter: additional particle parameter (optional)
```json
{
	"id": 1,
	"name": "Simple Particle",
	"color": "#FFFFFF",
	"detail": {
		PARTICLE PARAMETER,
		parameter: {
			ADDITIONAL PARTICLE PARAMETER
		}
	}
}
```

### Particle parameter

- particleType: particle type
- count: Number of particles generated in a single generation
- initDelay: Delay before the first particle generation
- interval: Interval between successive repeat generations
- repeat: Total number of generations. Will generate at least once
- offset: Initial offset position of the particle from the spawn point
- offsetRange: Random offset range around the initial position
- exactOffset: Whether to calculate the offset precisely on the server side
> Enabling this with a large _count_ will significantly impact network bandwidth
```json
"detail": {
	"particleType": "minecraft:explosion",
	"count": 1,
	"lifeTime": 200,
	"initDelay": 20,
	"interval": 20,
	"repeat": 10,
	"offset": "0.0,0.0,0.0",
	"offsetRange": "0.0,0.0,0.0",
	"exactOffset": false
}
```

### Additional particle parameter

These parameters are only effective for specific particle types

- speed: Controls particle initial speed
- color: Particle color for _minecraft:dust_
- scale: Particle size for _minecraft:dust_
- note: Category for _minecraft:note_ particles
- nbt: Custom NBT data for the particle, including parsing for "item" and "block_state" for their respective particle types
```json
"detail": {
	PARTICLE PARAMETER,
	"parameter": {
		"speed": 0.1,
		"color": "#0000FF",
		"scale": 1.0,
		"note": 0,
		"nbt": "{}"
	}
}
```