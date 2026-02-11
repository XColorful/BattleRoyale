[English](#English)

# 编辑规范

> 本文档列出了所有文档编写时必须遵守的格式与风格规范

## 通用

- 中英双语：所有面向用户的文档（如 Wiki）首行必须添加`[English](#English)`跳转
- 标题：禁止使用数字列表（如`1.`、`2.`），禁止中英混排
- 人称：禁止使用“我们”等第一人称，应采用客观的展示或叙说口吻
- 链接：采用`[链接文本](链接地址)`格式，链接文本不受文件名格式或驼峰命名法约束
- Obsidian 专用语法：禁止使用`[[双链]]`、`==高亮==`等非标准 Markdown 语法

### 语言工程

- 用软件工程重构“长难句”
- 语言架构扁平化

## 格式与标点
> 标题下的`>`引用块后不用空行

- 列表：列表项的左值禁止加粗
- 空行：
	- 包含嵌套列表项后不用空行
	- 普通段落之间空一行
- 句号：
	- 中文短句（告示牌性质）后不加句号
    - 句末可用冒号就不用句号
	- 两句话能用封号隔开就不用句号
	- 一行必须有逗号、不适用封号，才能有两个句号
	- 无法避免多句话时，每句都正常句号结尾
	- 英文不管，句号风格在上下文同构的段落中保持统一即可
- 标点符号：
	- 中文部分使用全角标点（：，（））
	- 英文部分使用半角标点（: , ()）
- 特殊格式：
	- _Minecraft_ 和文件路径（如 _./minecraft/config_）用下划线包围
    - 英文 English 和中文间加空格，已有文档中未加的可以更改
    - 下划线（斜体）、English 遇到中文符号（括号、逗号等）不用额外加空格
	- `代码`和`强调文字`之间禁止添加空格
	> 错误示范：`GameManager` 和 `ModConfigManager`
	> 正确示范：`GameManager`和`ModConfigManager`
- 公式：
	- 使用 LaTeX 格式，例如 $O(1)$
	- 公式与前后文字之间必须有空格
- 缩进：
	- 列表下的第一层缩进使用单个 Tab
	- 列表项内容禁止换行
- 列举：
	- 行末用冒号（：），否则以句号+空行以表明非关联性
	- 如果冒号结尾的行本身不是列表项，则其下的列表项无需进一步缩进

## 目录与文件命名

### 架构文档
> ./docs/architecture

面向 Github 在线查阅/Intellij IDEA 里查阅
- 文件名：全部小写，用横杠（-）分隔
- 英文标题：文件名即为英文标题

### Wiki 文档
> ./docs/wiki

Github Wiki，主要面向服主（玩家只需`/cbr team`指令）
- 文件名：可包含大写，用空格分隔
- 英文标题：文件名即为 GitHub Wiki 上显示的英文标题

# English

> This document lists the formatting and stylistic standards that must be observed when writing all documentation.

## General

- Bilingualism: All user-facing documents (e.g., Wiki) must include the `[English](#English)` jump link on the first line.
- Headings: Do not use numbered lists (e.g., `1.`, `2.`) for headings; mixing Chinese and English in a single heading is prohibited.
- Perspective: Do not use the first person (e.g., "we"); use an objective, descriptive tone.
- Links: Use the `[Link Text](URL)` format; link text should not be limited by file formats or CamelCase.
- Obsidian Syntax: Prohibited from using non-standard Markdown like `[[Links]]` or `==Highlight==`.

### Language Engineering

- Refactor "long and complex sentences" using software engineering principles.
- Flatten the linguistic architecture.

## Formatting & Punctuation
> No empty line is required after a `>` quote block following a heading.

- Lists: Bold formatting is prohibited for the left-hand values of list items.
- Spacing:
	- Do not use empty lines after nested list items.
	- Use a single empty line between standard paragraphs.
- Periods:
	- (Only in Chinese) Do not add periods after short Chinese phrases (e.g., signs/notices).
	- Use colons instead of periods where possible at the end of a sentence.
	- Use semicolons instead of periods to separate two sentences.
    - If an entry must have a comma and semicolons are not applicable, it may have two periods.
	- If multiple sentences are unavoidable, each should end with a period.
	- For English text, maintain consistent period usage within isomorphic paragraphs.
- Punctuation:
	- Use full-width punctuation for Chinese sections (：，（）).
	- Use half-width punctuation for English sections (: , ()).
- Special Formatting:
	- Surround _Minecraft_ and file paths (e.g., _./minecraft/config_) with underscores.
	- Add a space between English and Chinese text; existing documents can be updated accordingly.
	- No space is required between underscores (italics)/English and Chinese punctuation (brackets, commas, etc.).
	- (Only in Chinese) Do not add spaces between `code` and `emphasized text`.
    > Incorrect: `GameManager` 和 `ModConfigManager`
    > Correct: `GameManager`和`ModConfigManager`
- Formulas:
	- Use LaTeX format, e.g., $O(1)$.
	- Spaces must be added between formulas and surrounding text.
- Indentation:
	- Use a single Tab for the first level of indentation under lists.
	- Do not use line breaks within list item content.
- Enumeration:
	- Use a colon (:) at the end of the line; otherwise, use a period + empty line to indicate non-correlation.
	- If the line ending with a colon is not a list item, the list items below do not require further indentation.

## Directory & File Naming

### Architecture Docs
> ./docs/architecture

Targeted for GitHub online viewing and IntelliJ IDEA.
- Filenames: All lowercase, separated by hyphens (-).
- English Title: The filename serves as the English title.

### Wiki Docs
> ./docs/wiki

GitHub Wiki, primarily targeted for server owners (Player only need the `/cbr team` command).
- Filenames: Capitalization allowed, separated by spaces.
- English Title: The filename is displayed as the title on GitHub Wiki.