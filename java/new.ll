; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)
declare i32 @scanf(i8* noalias nocapture, ...)

; Actual code begins

@"formatRead2" = global [3 x i8] c"%d\00"
@"formatPrint1" = global [3 x i8] c"%d\00"

define i32 @test(){
; Début block 
%b = alloca i32
call i32 (i8*,...) @scanf(i8* getelementptr inbounds ([3 x i8],[3 x i8]* @formatRead2, i32 0, i32 0), i32* %b)
%tmp3 = load i32, i32* %b
ret i32 %tmp3
; Fin block 
ret i32 0
}


define void @main(){
; Début block 
%x = alloca i32
%tmp1 = call i32 @test()
store i32 %tmp1, i32* %x
%tmp2 = load i32, i32* %x
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([3 x i8],[3 x i8]* @formatPrint1, i32 0, i32 0), i32 %tmp2)
; Fin block 
ret void
}

