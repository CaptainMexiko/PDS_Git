; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins

@"formatPrint1" = global [3 x i8] c"%d\00"


define void @main(){
; Début block 
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([3 x i8],[3 x i8]* @formatPrint1, i32 0, i32 0), i32 1)
; Fin block 
ret void
}
