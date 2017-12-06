; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins

@"formatPrint2" = global [5 x i8] c"test\00"
@"formatPrint3" = global [7 x i8] c"coucou\00"
@"formatPrint1" = global [29 x i8] c"x vaut : %d mais b vaut : %d\00"

define void @test(){
; Début block 
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([5 x i8],[5 x i8]* @formatPrint2, i32 0, i32 0))
; Fin block 
ret void
}

define void @test2(){
; Début block 
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([7 x i8],[7 x i8]* @formatPrint3, i32 0, i32 0))
; Fin block 
ret void
}


define i32 @main(){
; Début block 
%x = alloca i32
%b = alloca i32
%tmp1 = add i32 2, 3
store i32 %tmp1, i32*  %x
store i32 0, i32*  %b
%tmp2 = load i32, i32* %x
%tmp3 = add i32 %tmp2, 4
%tmp4 = load i32, i32* %b
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([29 x i8],[29 x i8]* @formatPrint1, i32 0, i32 0), i32 %tmp3, i32 %tmp4)
%tmp5 = load i32, i32* %x
ret i32 %tmp5
; Fin block 
ret i32 0
}

