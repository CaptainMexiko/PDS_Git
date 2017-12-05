; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins

@"formatPrint1" = global [12 x i8] c"Hello World\00"


define i32 @main(){
; Début block 
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([12 x i8],[12 x i8]* @formatPrint1, i32 0, i32 0))
; Fin block 
ret i32 0
}
