# 部署环境

|流程|操作概览|说明|
|:--|:--|:--|
|👉 [**部署环境**](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment)|**下载Java、Minecraft启动器、Forge、IntellijIDEA**|**对新手入门不必深入了解，专注于开发实践即可**|
|[确定开发方向](https://github.com/XColorful/BattleRoyale/wiki/Determine-Direction)|确定模组开发内容（打算做新方块/物品/机制/其他？），编写模组文档，配置Gradle|避免盲目地开发，站在前人的肩膀上以简化起步流程|
|[完成最小可用模组](https://github.com/XColorful/BattleRoyale/wiki/Minimum-Viable-Mod)|完成模组入口|第一个可运行版本，即使没有实质功能，却已经成功一半|
|[完成一个功能](https://github.com/XColorful/BattleRoyale/wiki/Complete-a-Function)|创建一个方块/指令/物品/新机制|及时检验成果，并以此激励自己深入学习|
|[完成更多功能](https://github.com/XColorful/BattleRoyale/wiki/Complete-More-Functions)|完整剩余计划要完成的内容，涉及更多领域|丰富经验，提高开发熟练度|
|[整体架构分析](https://github.com/XColorful/BattleRoyale/wiki/Overall-Architecture-Analysis)|根据前几步积累的开发经验，形成一个整体系统认识|正式进入开发流程|

这个部分**不必要深入学习任何内容**，可以作为“备忘录”而不需要记忆：
- 本教程致力于研究如何不厌其烦地将“已经熟悉的流程”展示出来，并让新人能**方便且舒适**地完成
- 本教程将包含外部资料的[超链接](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment)，对[超链接](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment)按 _鼠标中键/右键_ 可以在新标签页打开
- 引用外部资料既避免重复“造轮子”，同时也是可积累的公共资料来源，为后续“遇到困难先找前人经验”养成习惯

> 小明去学校前按照老师给的清单买了几只笔、一块橡皮、一把尺子……放进书包里，之后每天到教室学习的时候，需要用笔就从书包里拿笔，需要用尺就从书包里拿尺，而不再需要考虑哪些没买
> 
> 本页教程正是如此

## 文字流程

> 本教程各部分都会先给出“文字流程”：
> - 对于新人：用于快速了解一共需要做哪些事，以及注意事项
> - 对于有经验的读者：能快速学习新内容并独立完成，不想听“啰嗦”的手把手教程

1. 确定模组运行的 _Minecraft_ 版本，找到对应的[`最低要求Java版本`](https://zh.minecraft.wiki/w/Tutorial:更新Java#选择Java版本)及其`官启绑定Java版本`😀
2. 根据`最低要求Java版本`选择[Java SE](https://www.oracle.com/java/technologies/downloads/archive/)，找到`官启绑定Java版本`对应的Java SE Development Kit并下载安装😀
3. 下载安装 [_Minecraft_ 官方启动器](https://www.minecraft.net/zh-hans/download)，运行 _Minecraft_ 官方启动器并用[微软账号](https://account.microsoft.com/account)登录（需要购买游戏）
4. 下载[Forge](https://files.minecraftforge.net/net/minecraftforge/forge/)+MDK（_Minecraft_ 版本≤1.20.1） / [NeoForge](https://neoforged.net/)+[NeoGradle](https://github.com/NeoForgeMDKs/)（_Minecraft_ 版本＞1.20.1），运行后选择`Install client`（默认），点击`OK` / `Proceed`安装😀
5. 下载安装[IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/)（注意**不是第一个**`Download`）

## 详细流程

### ⭐确定Minecraft版本 (1️⃣/5)

> （可跳过！）内容太多一点也不想看？直接给一个方案：😀
> 
> |_Minecraft_ 版本|`最低要求Java版本`|`官启绑定Java版本`|
> |:--|:--|:--|
> |1.20.1|17|17.0.8|

---

按以下建议对号入座：

|情况|怎么做|原因|
|:--|:--|:--|
|1. **我不知道什么是模组 / 整合包**（不用担心，暂时不需了解）|选择**1.20.1**✅|1.20.1不仅是一个**热门版本**，本模组（核心版本一致）及发展过程可作为教程的实例参考|
|2. 我目前没有想联动的模组，但是最好能有多点好玩的模组|尝试搜索**热门版本**（如1.20.1、1.21.1、1.12.2等）并寻找模组/**整合包**，转到第3种或第4种情况|**热门版本**往往有大量模组并建立起生态，模组开发者多会支持该版本|
|3. 我特别想跟某个模组/整合包一起玩|寻找该模组/整合包所支持的版本，若有**热门版本**则优先选择该版本|在版本相同的基础上，热门版本（生态）将来可能有其他模组加入你的游戏|
|4. 我觉得哪个版本无所谓 / 我想在 _Minecraft_ 原版风格的基础上写模组|直接选择**最新版本**，或搜索Vanilla+整合包支持哪些版本（通常也都支持最新版本）|不妨向新事物看齐，将来若有重大更新吸引你时，移植模组也方便|

> 本模组最初写在1.20.1就是为了跟[TaCZ](https://github.com/MCModderAnchor/TACZ)以实现 _大逃杀 + 枪械 = 枪械吃鸡_，属于第3种情况

##### Minecraft版本表格

|_Minecraft_ 版本|最低要求Java版本|官启绑定Java版本|
|:--|:--|:--|
|1.20.5-最新|Java21|21.0.7|
|1.19-1.20.4|Java17|17.0.8|
|1.18-1.18.2|Java17|17.0.1|
|1.17-1.17.1|Java16|16.0.1|
|最旧-1.16.5|Java8|1.8.0_51|

> 表格转载自 _https://zh.minecraft.wiki/w/Tutorial:更新Java#选择Java版本_ （[CC BY-NC-SA 3.0](https://creativecommons.org/licenses/by-nc-sa/3.0/)）

在以上[表格](https://zh.minecraft.wiki/w/Tutorial:更新Java#选择Java版本)中找到对应的`最低要求Java版本`及其`官启绑定Java版本`

### ⭐下载Java SE (2️⃣/5)

> （可跳过！）1/5 中开头提到的方案只需下载安装[Java SE 17.0.8](https://download.oracle.com/java/17/archive/jdk-17.0.8_windows-x64_bin.exe)😀

---

例如，本模组核心代码写在 _Minecraft_ 1.20.1，属于`1.19-1.20.4`：
- 对应`最低要求Java版本`为 _Java **17**_，`官启绑定Java版本`为 _17.0.8_
- 在[Java SE](https://www.oracle.com/java/technologies/downloads/archive/)界面点击右侧[Java SE **17** (17.0.12 and earlier)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)，在页面上搜索到Java SE Development Kit **17.0.8**并下载安装（对于Windows系统，下载Windows x64 Installer 153.48 MB）

### ⭐下载Minecraft启动器 (3️⃣/5)

> 这部分无法跳过(⊙﹏⊙)，但已经过半了🥳

---

先注册一个[微软账号](https://account.microsoft.com/account)（如果没有），购买[Minecraft：Java 版和 Bedrock 版](https://www.minecraft.net/zh-hans/store/minecraft-java-bedrock-edition-pc)
> 如果不希望购买游戏，请**自行搜索**其他启动器及代替教程，这是可以理解但**不支持**的

下载安装 [_Minecraft_ 官方启动器](https://www.minecraft.net/zh-hans/download)，启动后用刚才购买 _Minecraft_ 的[微软账号](https://account.microsoft.com/account)登录，进入启动器界面
- 必需**成功启动一次**官方启动器才能继续后续步骤

### ⭐下载模组平台 (4️⃣/5)

---

如果你选择的 _Minecraft_ 版本≤1.20.1，则下载Forge，>1.20.1则下载NeoForge
> 实际上还有[Fabric](https://wiki.fabricmc.net/zh_cn:start)等其他平台，建议**有一定开发经验**后再学习/移植其他平台

#### 下载Forge

> 1/5 中开头提到的方案的下载文件[Installer](https://adfoc.us/serve/?id=271228112108276)、[Mdk](https://adfoc.us/serve/?id=271228112150305)（点击右上角的红色`SKIP`）😀

在Forge[下载页面](https://files.minecraftforge.net/net/minecraftforge/forge/)左侧选择之前确定的 _Minecraft_ 版本，分别点击 _Download Latest_ 下的`Installer`和`Mdk`，得到两个文件：
- _forge-1.x.x-x.x.x-installer.jar_
- _forge-1.x.x-x.x.x-mdk.zip_

点击 _forge-1.x.x-x.x.x-installer.jar_，选择`Install client`（默认），点`OK`安装
- 如果界面有红字提示，则需要先下载并**启动一次**官方启动器
- 下载时可能出现网络问题导致下载中断，多次尝试后仍不能安装则表明所在的地区不支持下载（这不是你的问题，但需要上网寻求解决办法）

解压 _forge-1.x.x-x.x.x-mdk.zip_ 到文件夹并重命名，该文件夹即模组项目根目录

#### 下载NeoForge

在NeoForge MDK[下载页面](https://github.com/NeoForgeMDKs/)找到`Repositories`，搜索之前确定的 _Minecraft_ 版本（如1.21.1），点击带`NeoGradle`的版本（如[MDK-1.21.1-NeoGradle](https://github.com/NeoForgeMDKs/MDK-1.21.1-NeoGradle)）后：
- 点击界面中绿色的`Code`
- 点击`Download ZIP`
- 得到一个文件：_MDK-1.x.x-NeoGradle-main.zip_

解压 _MDK-1.x.x-NeoGradle-main.zip_ 并将解压得到的文件夹重命名，该文件夹即模组项目根目录，
- 找到根目录下的 _gradle.properties_ ，其中有一行`neo_version=x.x.x`
```
...
minecraft_version_range=[x.x.x]
# The Neo version must agree with the Minecraft version to get a valid artifact
neo_version=x.x.x
# The loader version range can only use the major version of FML as bounds
loader_version_range=[1,)
...
```
在NeoForge[下载页面](https://neoforged.net/)中点击`Minecraft Version`右侧的框并选择之前确定的 _Minecraft_ 版本，点击`NeoForge Version`右侧的框并选择之前的`neo_version`，点击右侧`Click Here to Download Installer`，得到一个文件：
- _neoforge-x.x.x-installer.jar_

点击 _neoforge-x.x.x-installer.jar_，选择`Install client`（默认），点`Proceed`安装
- 如果界面有红字提示，则需要先下载并**启动一次官方启动器**
- 下载时可能出现网络问题导致下载中断，多次尝试后仍不能安装则表明所在的地区不支持下载（这不是你的问题，但需要上网寻求解决办法）

### ⭐下载IDE (5️⃣/5)

> 这部分无法跳过(⊙﹏⊙)，但已经是最后一步了🥳

---

在Intellij IDEA[下载页面](https://www.jetbrains.com/idea/download/)，往下找到`IntelliJ IDEA Community Edition`，点击`Download`下载并安装

打开Intellij IDEA：
1. 点击`Open`，选择模组项目根目录下的`build.gradle`，点击`Open as Project`
2. 点击右上角的齿轮，点击`Project Structure...`，在`SDK`中选择`下载Java SE`的版本，`Language Level`选择 _SDK default_，点击下方`OK`
3. 点击右上角的齿轮，点击`Settings...`，点击左侧`Plugins`，上方点击`Marketplace`，搜索`Minecraft Development`，点击绿色的`Install`，点击下方`OK`
4. 点击右侧的大象（Gradle），点击左侧第一个`Sync All Gradle Projects`
5. 等待右下角`Import 'xxx' Gradle Project`（可能会花很长时间），做点别的事情放松一下🐟
6. 若执行`步骤4`后在下方有弹出一堆红色文字，重复步骤4，多次尝试后仍就如此的话，表明所在的地区不支持下载（这不是你的问题，但需要上网寻求解决办法）

如果成功执行完`步骤4`，那么恭喜，你已经成功部署模组开发环境✔，可以开始[确定开发方向](https://github.com/XColorful/BattleRoyale/wiki/Determine-Direction)了😄🎉

# English

|Process|Operation Overview|Explanation|
|:--|:--|:--|
|👉 [**Deploy Environment**](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment#English)|**Download Java, Minecraft Launcher, Forge, IntelliJ IDEA.**|**Beginners don't need deep understanding; focus on development practice.**|
|[Determine Direction](https://github.com/XColorful/BattleRoyale/wiki/Determine-Direction#English)|Decide on mod content (New blocks/items/mechanics?), write mod documentation, configure Gradle.|Avoid blind development; stand on the shoulders of giants to simplify the start-up process.|
|[Minimum Viable Mod](https://github.com/XColorful/BattleRoyale/wiki/Minimum-Viable-Mod#English)|Complete the mod entry point.|The first runnable version; even without substantial functions, it's half the success.|
|[Complete a Function](https://github.com/XColorful/BattleRoyale/wiki/Complete-a-Function#English)|Create a block/command/item/new mechanism.|Verify results in time and use this to motivate deeper learning.|
|[Complete More Functions](https://github.com/XColorful/BattleRoyale/wiki/Complete-More-Functions#English)|Finish the remaining planned content, involving more fields.|Enrich experience and improve proficiency.|
|[Overall Architecture Analysis](https://github.com/XColorful/BattleRoyale/wiki/Overall-Architecture-Analysis#English)|Form a systematic understanding based on experience accumulated in previous steps.|Officially enter the development flow.|

This section **does not require deep learning of any content**; it can serve as a "memo" without requiring memorization:
- This tutorial aims to patiently present a "familiar process" to make it **convenient and comfortable** for newcomers to complete.
- This tutorial includes [hyperlinks](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment#English) to external resources. Click on the [hyperlink](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment#English) with the _middle/right mouse button_ to open it in a new tab.
- Citing external resources avoids "reinventing the wheel" and utilizes a cumulative public source of information, fostering the habit of **"seeking prior experience when facing difficulties."**

> Alex went to school and bought a few pens, an eraser, a ruler... according to the teacher's checklist and put them in his backpack. Every day he goes to class, he takes out a pen when he needs a pen, and a ruler when he needs a ruler, without having to worry about what he hasn't bought.
> 
> This page of the tutorial is just like that.

## Text Flow

> Each part of this tutorial will first provide the "Text Flow":
> - For beginners: Used to quickly understand what needs to be done and the precautions.
> - For experienced readers: Enables fast learning of new content and independent completion, without the need for a "verbose" step-by-step guide.

1. Determine the _Minecraft_ version the mod will run on, find the corresponding [`Minimum Java version`](https://minecraft.wiki/w/Tutorial:Update_Java#Why_update.3F) and the [`Official Launcher Bound Java Version`](https://github.com/XColorful/BattleRoyale/wiki/Deploy-Environment#Minecraft-Version-Table).😀
2. Based on the `Minimum Java version`, select [Java SE](https://www.oracle.com/java/technologies/downloads/archive/), find the corresponding Java SE Development Kit for the `Official Launcher Bound Java Version`, and download and install it.😀
3. Download and install the [_Minecraft_ Official Launcher](https://www.minecraft.net/en-us/download), run the _Minecraft_ Official Launcher, and log in with a [Microsoft Account](https://account.microsoft.com/account) (requires purchasing the game).
4. Download [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/)+MDK (_Minecraft_ version ≤ 1.20.1) / [NeoForge](https://neoforged.net/)+[NeoGradle](https://github.com/NeoForgeMDKs/) (_Minecraft_ version > 1.20.1), run it, select `Install client` (default), and click `OK` / `Proceed` to install.😀
5. Download and install [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/) (Note: **not the first** `Download`).

## Detailed Flow

### ⭐Determine Minecraft Version (1️⃣/5)

> (Optional!) Too much content and don't want to read it? Here's a direct solution: 😀
> 
> |_Minecraft_ Version|`Minimum Java version`|`Official Launcher Bound Java Version`|
> |:--|:--|:--|
> |1.20.1|17|17.0.8|

---

Match your situation to the suggestions below:

|Situation|What to do|Reason|
|:--|:--|:--|
|1. **I don't know what mods / modpacks are** (Don't worry, no need to understand yet)|Select **1.20.1**✅|1.20.1 is a **popular version**, and this mod (core version alignment) and its development process can serve as a tutorial instance.|
|2. I don't plan to link with any specific mods, but I want to have more fun mods.|Try searching for **popular versions** (like 1.20.1, 1.21.1, 1.12.2, etc.) and look for mods/**modpacks**. Go to situation 3 or 4.|**Popular versions** often have a large number of mods and an established ecosystem, which most mod developers support.|
|3. I really want to play with a specific mod/modpack.|Find the version supported by that mod/modpack. If a **popular version** is supported, prioritize it.|Based on version compatibility, a popular version (ecosystem) may allow other mods to join your game in the future.|
|4. I don't care which version / I want to write a mod based on the vanilla _Minecraft_ style.|Directly choose the **latest version**, or search for which versions Vanilla+ modpacks support (usually the latest version).|It is worthwhile to look towards new developments; porting your mod will be easier if a major future update attracts you.|

> The core code for this mod was initially written for _Minecraft_ 1.20.1 to link with [TaCZ](https://github.com/MCModderAnchor/TACZ) to achieve _Battle Royale + Guns = Gun Chicken Dinner_, falling into situation 3.

##### Minecraft Version Table

|_Minecraft_ Version|Minimum Required Java Version|Official Launcher Bound Java Version|
|:--|:--|:--|
|1.20.5-Latest|Java 21|21.0.7|
|1.19-1.20.4|Java 17|17.0.8|
|1.18-1.18.2|Java 17|17.0.1|
|1.17-1.17.1|Java 16|16.0.1|
|Oldest-1.16.5|Java 8|1.8.0_51|

> Table reproduced from _https://zh.minecraft.wiki/w/Tutorial:更新Java#选择Java版本_ ([CC BY-NC-SA 3.0](https://creativecommons.org/licenses/by-nc-sa/3.0/)) (Note: This external link leads to the Chinese Minecraft Wiki, as the corresponding English page lacks this specific table.)

Find the corresponding `minimum Java version` and `Official Launcher Bound Java Version` in the [table](https://zh.minecraft.wiki/w/Tutorial:更新Java#选择Java版本) above.

### ⭐Download Java SE (2️⃣/5)

> (Optional!) The solution mentioned at the start of 1/5 only requires downloading and installing [Java SE 17.0.8](https://download.oracle.com/java/17/archive/jdk-17.0.8_windows-x64_bin.exe)😀

---

For example, the core code for this mod was written for _Minecraft_ 1.20.1, which belongs to `1.19-1.20.4`:
- Corresponding `Minimum Java version` is _Java **17**_, and `Official Launcher Bound Java Version` is _17.0.8_.
- On the [Java SE](https://www.oracle.com/java/technologies/downloads/archive/) interface, click on the right-side link [Java SE **17** (17.0.12 and earlier)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html), search for Java SE Development Kit **17.0.8** on the page, and download and install it (For Windows systems, download Windows x64 Installer 153.48 MB).

### ⭐Download Minecraft Launcher (3️⃣/5)

---

> This part cannot be skipped (⊙﹏⊙), but you're already halfway there!🥳

First, register a [Microsoft Account](https://account.microsoft.com/account) (if you don't have one) and purchase [Minecraft: Java & Bedrock Edition for PC](https://www.minecraft.net/en-us/store/minecraft-java-bedrock-edition-pc).
> If you do not wish to purchase the game, please **search for** other launchers and alternative tutorials yourself. This is understandable but **not supported** here.

Download and install the [_Minecraft_ Official Launcher](https://www.minecraft.net/en-us/download). Launch it and log in with the [Microsoft Account](https://account.microsoft.com/account) used to purchase _Minecraft_. Enter the launcher interface.
- You **must successfully launch** the official launcher once before proceeding to the next steps.

### ⭐Download Mod Platform (4️⃣/5)

---

If your chosen _Minecraft_ version is ≤ 1.20.1, download Forge; if it is > 1.20.1, download NeoForge.
> There are actually other platforms like [Fabric](https://wiki.fabricmc.net/start), but it is recommended to study/port to other platforms **after gaining some development experience**.

#### Download Forge

> The download files for the solution mentioned at the start of 1/5: [Installer](https://adfoc.us/serve/?id=271228112108276), [Mdk](https://adfoc.us/serve/?id=271228112150305) (Click the red `SKIP` button in the upper right corner)😀

On the Forge [download page](https://files.minecraftforge.net/net/minecraftforge/forge/), select the previously determined _Minecraft_ version on the left, and click on `Installer` and `Mdk` under _Download Latest_ (Click the red `SKIP` button in the upper right corner), resulting in two files:
- _forge-1.x.x-x.x.x-installer.jar_
- _forge-1.x.x-x.x.x-mdk.zip_

Click on _forge-1.x.x-x.x.x-installer.jar_, select `Install client` (default), and click `OK` to install.
- If the interface shows a red warning, you need to first download and **launch the official launcher once**.
- A network issue may cause the download to interrupt. If multiple attempts still fail to install, it indicates that your region does not support the download (This is not your fault, but you need to seek solutions online).

Unzip _forge-1.x.x-x.x.x-mdk.zip_ into a folder and rename it. This folder is your mod project root directory.

#### Download NeoForge

On the NeoForge MDK [download page](https://github.com/NeoForgeMDKs/), find `Repositories`, search for the previously determined _Minecraft_ version (e.g., 1.21.1), click the version with `NeoGradle` (e.g., [MDK-1.21.1-NeoGradle](https://github.com/NeoForgeMDKs/MDK-1.21.1-NeoGradle)), then:
- Click the green `Code` button on the interface.
- Click `Download ZIP`.
- This results in a file: _MDK-1.x.x-NeoGradle-main.zip_.

Unzip _MDK-1.x.x-NeoGradle-main.zip_ and rename the resulting folder. This folder is your mod project root directory.
- Find _gradle.properties_ in the root directory, which contains a line `neo_version=x.x.x`:
```
...
minecraft_version_range=[x.x.x]
# The Neo version must agree with the Minecraft version to get a valid artifact
neo_version=x.x.x
# The loader version range can only use the major version of FML as bounds
loader_version_range=[1,)
...
```
On the NeoForge [download page](https://neoforged.net/), click the box to the right of `Minecraft Version` and select your previously determined _Minecraft_ version. Click the box to the right of `NeoForge Version` and select the previous `neo_version`. Click `Click Here to Download Installer` on the right, resulting in a file:
- _neoforge-x.x.x-installer.jar_

Click on _neoforge-x.x.x-installer.jar_, select `Install client` (default), and click `Proceed` to install.
- If the interface shows a red warning, you need to first download and **launch the official launcher once**.
- A network issue may cause the download to interrupt. If multiple attempts still fail to install, it indicates that your region does not support the download (This is not your fault, but you need to seek solutions online).

### ⭐Download IDE (5️⃣/5)

> This part cannot be skipped (⊙﹏⊙), but it's the final step!🥳

---

On the IntelliJ IDEA [download page](https://www.jetbrains.com/idea/download/), scroll down to `IntelliJ IDEA Community Edition`, click `Download`, and install it.

Open IntelliJ IDEA:
1. Click `Open`, select the `build.gradle` file in your mod project root directory, and click `Open as Project`.
2. Click the gear icon in the top right, click `Project Structure...`, select the version from `Download Java SE` for `SDK`, choose _SDK default_ for `Language Level`, and click `OK` at the bottom.
3. Click the gear icon in the top right, click `Settings...`, click `Plugins` on the left, click `Marketplace` at the top, search for `Minecraft Development`, click the green `Install`, and click `OK` at the bottom.
4. Click the elephant icon (Gradle) on the right, and click the first icon on the left, `Sync All Gradle Projects`.
5. Wait for `Import 'xxx' Gradle Project` in the bottom right (This may take a long time). Take a break and do something else 🐟.
6. If red text appears below after executing `Step 4`, repeat Step 4. If this persists after multiple attempts, it indicates that your region does not support the download (This is not your fault, but you need to seek solutions online).

If `Step 4` executed successfully, congratulations, you have successfully deployed the mod development environment ✔. You can now start [Determine Direction](https://github.com/XColorful/BattleRoyale/wiki/Determine-Direction#English)!😄🎉