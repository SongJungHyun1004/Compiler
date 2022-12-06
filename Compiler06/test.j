.class public Test
.super java/lang/Object
.method public <init>()V
aload_0
invokenonvirtual java/lang/Object/<init>()V
return
.end method
.method public static add(II)I
	.limit stack 32
	.limit locals 32
iload_0
iload_1
iadd 
istore_2
iload_2
ldc 2
iadd 
ireturn
.end method
.method public static p2()V
	.limit stack 32
	.limit locals 32
getstatic java/lang/System/out Ljava/io/PrintStream; 
ldc 2
invokevirtual java/io/PrintStream/println(I)V
getstatic java/lang/System/out Ljava/io/PrintStream; 
ldc 2
invokevirtual java/io/PrintStream/println(I)V
return
.end method
.method public static foo(I)I
	.limit stack 32
	.limit locals 32
iload_0
ldc 5
isub 
iflt label0
ldc 0
goto label1
label0:
ldc 1
label1:
ifeq label3
ldc 3
ireturn
goto label2
label3:
ldc 4
ireturn
label2:
return
.end method
.method public static multiIF(II)V
	.limit stack 32
	.limit locals 32
iload_0
ldc 10
isub 
ifeq label0
ldc 0
goto label1
label0:
ldc 1
label1:
ifeq label9
getstatic java/lang/System/out Ljava/io/PrintStream; 
ldc 1
invokevirtual java/io/PrintStream/println(I)V
iload_1
ldc 3
isub 
ifne label2
ldc 0
goto label3
label2:
ldc 1
label3:
ifeq label7
getstatic java/lang/System/out Ljava/io/PrintStream; 
ldc 2
invokevirtual java/io/PrintStream/println(I)V
goto label6
label7:
getstatic java/lang/System/out Ljava/io/PrintStream; 
ldc 3
invokevirtual java/io/PrintStream/println(I)V
ldc 1
ifeq label4
getstatic java/lang/System/out Ljava/io/PrintStream; 
ldc 4
invokevirtual java/io/PrintStream/println(I)V
label4:
label6:
goto label8
label9:
getstatic java/lang/System/out Ljava/io/PrintStream; 
ldc 0
invokevirtual java/io/PrintStream/println(I)V
label8:
return
.end method
.method public static multiFOR(III)V
	.limit stack 32
	.limit locals 32
label14:
iload_0
iload_1
isub 
iflt label0
ldc 0
goto label1
label0:
ldc 1
label1:
ifeq label15
ldc 0
istore_3
label9:
iload_3
iload_1
isub 
iflt label2
ldc 0
goto label3
label2:
ldc 1
label3:
ifeq label10
iload_2
ldc 2
iadd 
istore_2
iload_3
ldc 1
iadd
istore_3
goto label9
label10:
iload_0
ldc 1
iadd
istore_0
goto label14
label15:
getstatic java/lang/System/out Ljava/io/PrintStream; 
iload_2
invokevirtual java/io/PrintStream/println(I)V
return
.end method
.method public static main([Ljava/lang/String;)V
	.limit stack 32
	.limit locals 32
ldc 100
istore_1 
ldc 200
istore_2 
ldc 10
istore_3 
ldc 3
istore 4 
ldc 0
istore 5 
ldc 10
istore 6 
ldc 0
istore 7 
getstatic java/lang/System/out Ljava/io/PrintStream; 
iload_1
iload_2
invokestatic Test/add(II)I
invokevirtual java/io/PrintStream/println(I)V
invokestatic Test/p2()V
getstatic java/lang/System/out Ljava/io/PrintStream; 
iload_3
invokestatic Test/foo(I)I
invokevirtual java/io/PrintStream/println(I)V
iload_3
iload 4
invokestatic Test/multiIF(II)V
iload 5
iload 6
iload 7
invokestatic Test/multiFOR(III)V
return
.end method
