func add(a int, b int) int {
    var c int
    c = a + b
    return c + 2
}

func p2() {
	_print(2)
	_print(2)
}

func foo(x int) int {
	if(x < 5)
		return 3
	else
		return 4
}

func multiIF(a int, b int) {
	if a == 10 {
    	_print(1)
    	if b != 3
    		_print(2)
    	else{
    		_print(3)
    		if(1)
    			_print(4)
    	}
    } else
    	_print(0)
}

func multiFOR(i int, max int, res int) {
	var j
	for i < max {
    	j = 0
    	for j < max{
    		res = res + 2
    		++j
    	}
    	++i
    }
    _print(res)
	return
}

func main() {
	var x int = 100
	var y int = 200
	
	var a = 10
    var b = 3
    
    var i = 0
    var max = 10
    var res int = 0
    
	_print(add(x, y))
	p2()
    _print(foo(a))
	multiIF(a, b)
    multiFOR(i, max, res)
}