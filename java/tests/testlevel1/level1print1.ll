; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins

@"formatPrint1" = global [6 x i8] c"Hello\00"


define void @main(){
; Début block 
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([6 x i8],[6 x i8]* @formatPrint1, i32 0, i32 0))
; Fin block 
ret void
}
