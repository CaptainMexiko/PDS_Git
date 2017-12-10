; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)
declare i32 @scanf(i8* noalias nocapture, ...)

; Actual code begins

@"formatRead1" = global [3 x i8] c"%d\00"
@"formatPrint2" = global [7 x i8] c"coucou\00"

define void @test(){
; Début block 
%b = alloca i32
br label %while4

while4:
%icmp1 = icmp ne i32 1, 0 
br i1 %icmp1, label %do5, label %done6

do5:
; Début block 
; Début du if
%tmp1 = load i32, i32* %b
%icmp2 = icmp ne i32 %tmp1, 0 
br i1 %icmp2, label %then1, label %else3

then1:
; Début block 
call i32 (i8*,...) @scanf(i8* getelementptr inbounds ([3 x i8],[3 x i8]* @formatRead1, i32 0, i32 0), i32* %b)
; Fin block 
br label %fi2

else3:
; Début block 
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([7 x i8],[7 x i8]* @formatPrint2, i32 0, i32 0))
; Fin block 
br label %fi2

fi2:
; Fin du if
; Fin block 
br label %while4

done6:
; Fin block 
ret void
}


define void @main(){
; Début block 
%x = alloca i32
store i32 3, i32*  %x
call void @test()
; Fin block 
ret void
}

