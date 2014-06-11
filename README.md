Exceller
==========

A csv to excel conversion tool.

Here is a quick example:

```
java -jar exceller.jar test.csv
```

Main options:
```
    -H
       Read headers from csv
       Default: false
    -columns
       String containing column patterns separated by semicolon
    -h
       String containing header names separated by semicolon
    -il
       Iterate last pattern. Apply last column pattern to the rest of the
       columns in the table
       Default: false
    -n
       Perform a slice. Retrieve only n columns from csv
       Default: 0
    -output
       Output path
    -help
       Displays help
```

Column pattern string format:
```
 <Pattern> ::= <TypeLetter><ExcelMask><Width>
 <TypeLetter> ::= B | D | H | N | T
 <ExcelMask> ::= <Needed excel mask> | ""
 <Width> ::= W<integer> | ""
```
B - Boolean
D -Date
H - Hyperlink
N - Number
T - Text

Pattern string example (Date type with yyyy-mm-dd mask and column width 7000):
```
Dyyyy-mm-ddW7000
```

If no pattern string is provided for a column then the column is rendered as text by default. If no width is provided then the column width is defined by its content.

For detailed examples see examples folder.