; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
; Début block 
%x = alloca i32
%b = alloca i32
%c = alloca i32
store i32 10, i32*  %x
%tmp1 = load i32, i32* %x
store i32 %tmp1, i32*  %b
br label %while4

while4:
%tmp2 = load i32, i32* %x
%icmp1 = icmp ne i32 %tmp2, 0 
br i1 %icmp1, label %do5, label %done6

do5:
; Début block 
br label %while1

while1:
%tmp3 = load i32, i32* %b
%icmp2 = icmp ne i32 %tmp3, 0 
br i1 %icmp2, label %do2, label %done3

do2:
; Début block 
%tmp4 = load i32, i32* %b
%tmp5 = sub i32 %tmp4, 1
store i32 %tmp5, i32*  %b
%tmp6 = load i32, i32* %c
%tmp7 = add i32 %tmp6, 1
store i32 %tmp7, i32*  %c
; Fin block 
br label %while1

done3:
%tmp8 = load i32, i32* %x
%tmp9 = sub i32 %tmp8, 1
store i32 %tmp9, i32*  %x
; Fin block 
br label %while4

done6:
ret i32 14
; Fin block 
}

