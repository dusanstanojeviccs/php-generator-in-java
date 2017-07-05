<?php

function fib($n) {
	$cur = 1;
	$prev = 0;

	for ($i = 0; $i < $n; $i++) {
		echo "<br>Before yield";
		yield $cur;

		$temp = $cur;
		$cur = $prev + $cur;
		$prev = $temp;
	}
};

$fibs = fib(9);

foreach ($fibs as $fib) {
	echo "<br>" . $fib;
}