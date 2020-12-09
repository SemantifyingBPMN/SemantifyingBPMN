# SemantifyingBPMN
A framework to semantify BPMN using DEMO business transaction pattern
An experimental prototype.

Requirements:
Firstly, verify if JAVA 8 is available, on your linux environment, using the command: 

    java -version

Then, to execute the generator of metro usage messages use the following command:

    java -jar SemantifyingBPMN-0.0.1.jar

The following options are available:
```
SemantifyingBPMN-0.0.1 --actors <filename> --tpt <filename> --tkdepend <filename> --output-file-txt <filename> --output-file-bpmn <filename>
Credits: Sérgio Guerreiro (2020) (github: https://github.com/SemantifyingBPMN/SemantifyingBPMN)

where the parameters are,
--actors: is a csv file with the list of actor roles and is mandatory. Composed of 2 fields, in each line, with actor role name and description:
 (e.g.: A01 - Customer ; The role that initiates the business process).
--tpt: is a csv file with the Transactor Product Table and is mandatory. Composed of 6 fields, in each line, with TK name, TK description, Actor role initiator, Actor role executor, Product kind , Product kind description:
 (e.g.: TK01; Sale completing ; A01 - Customer  ; A02 - Dispatcher ; PK01 ; [Product] is sold).
--tkdepend: is a csv file with the dependencies matrix N*N transactions and is mandatory. Composed of Strings with dependencies: RaP = Request after Promise pattern, RaE = Request after Execution, RaD = Request after Declare pattern.
 (e.g.:
             ; TK01  ; TK02  ; TK03 ; TK04
        TK01 ;       ;       ;      ;
        TK02 ; RaP   ;       ;      ;
        TK03 ;       ; RaE   ;      ;
        TK04 ;       ;       ; RaE  ;
 )
--tkview: is a csv file with view definition for each transaction per line, acceptable values are: HappyFlow | HappyFlowAndDeclinationsAndRejections | Complete. Optional.
 (e.g.
                TK01 ; HappyFlow
                TK02 ; HappyFlowAndDeclinationsAndRejections
                TK03 ; HappyFlowAndDeclinationsAndRejections
                TK04 ; Complete
 )
--output-file-txt: is a file to store the model in txt format. Optional.
--output-file-bpmn: is a file to store the BPMN model. Optional.
```

Commands example:

``` 
java -jar SemantifyingBPMN-0.0.1.jar --actors actorRolesSIMPLES.txt --tkdepend TKdependenciesSIMPLES.txt --tpt tptSIMPLES.txt --output-file-txt output --tkview tkviewSIMPLES.txt
java -jar SemantifyingBPMN-0.0.1.jar --actors actorRolesSIMPLES.txt --tkdepend TKdependenciesSIMPLES.txt --tpt tptSIMPLES.txt --output-file-txt output
```



