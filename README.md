# SemantifyingBPMN
A framework to semantify BPMN using DEMO business transaction pattern
An experimental prototype.

Requirements:
Firstly, verify if JAVA 8 is available, on your linux environment, using the command: 

    java -version

Then, to execute the semantify BPMN use the following command:

    java -jar SemantifyingBPMN-0.0.2.jar

The following options are available:
```
The usage of SemantifyingBPMN is the following.
SemantifyingBPMN-0.0.2 --actors <filename> --tpt <filename> --tkdepend <filename> --output-file-txt <filename> --output-file-bpmn <filename>
Credits: Sérgio Guerreiro (2021) (github: https://github.com/SemantifyingBPMN/SemantifyingBPMN)

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
--tkview: is a mandatory csv file with view definition for each transaction per line, acceptable values are: HappyFlow | HappyFlowAndDeclinationsAndRejections | Complete | Custom | CustomHappyFlowOnly. Default value is HappyFlow.
          The Custom value accepts extra detail for each transaction step, even empty ones.
 (e.g.
      TransactionKind  ; View   ; Request Decision ; Request ; Promise Decision ; Promise ; Decline ; After Decline Decision ; Execute ; Declare        ; Decision Accept ; Accept ; Reject ; Evaluate Rejection ; Stop
                  TK01 ; HappyFlow
                  TK02 ; HappyFlowAndDeclinationsAndRejections
                  TK03 ; Custom ;                  ; Pedido  ;                  ;         ; Executa ; how to decide          ; Produce ; Here it is     ; Valida resultado;        ; not ok ; decide reject      ; ok
                  TK04 ; Complete
                  TK05 ;
 )
--output-file-txt: is a file to store the model in txt format. Optional.
--output-file-bpmn: is a file to store the BPMN model. Optional.
```

Command example:

``` 
java -jar .\SemantifyingBPMN-0.0.2.jar --actors .\actorRoles-2021-01-18.txt --tpt .\tpt-2021-01-18.txt --tkdepend .\TKdependencies-2021-01-18.txt --output-file-bpmn collab.bpmn --tkview .\tkview-2021-01-18.txt
```



