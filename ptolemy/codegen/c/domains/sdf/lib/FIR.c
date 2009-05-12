/*** preinitBlock ***/
$targetType(input)* $actorSymbol(_data);
$elementTargetType(taps) $actorSymbol(_zero);
int $actorSymbol(_mostRecent);
int $actorSymbol(_phaseLength);
$targetType(input) $actorSymbol(_outToken);
$elementTargetType(taps) $actorSymbol(_tapItem);
$targetType(input) $actorSymbol(_dataItem);
Token $actorSymbol(_taps);
int $actorSymbol(length);
int $actorSymbol(newLength);
/**/

/*** sharedBlock ***/
int $actorClass(inC);
int $actorClass(phase);
int $actorClass(dataIndex);
int $actorClass(tapsIndex);
int $actorClass(i);
int $actorClass(bufferIndex);   // for output offset in a single firing.
int $actorClass(inputIndex);        // for input offset.
/**/

/*** initBlock0 ***/
$actorSymbol(_taps) = $ref(taps);
/**/

/*** initBlock ***/
$actorSymbol(_zero) = $zero_$elementType(taps)($cgType(taps)_get($actorSymbol(_taps), 0));

$actorSymbol(_phaseLength) = $actorSymbol(_taps).payload.$cgType(taps)->size / $val(interpolation);

if (($actorSymbol(_taps).payload.$cgType(taps)->size % $val(interpolation)) != 0) {
	$actorSymbol(_phaseLength)++;
}

// Create new data array and initialize index into it.
// Avoid losing the data if possible.
// NOTE: If the filter length increases, then it is impossible
// to correctly initialize the delay line to contain previously
// seen data, because that data has not been saved.
$actorSymbol(length) = $actorSymbol(_phaseLength) + $val(decimation);

$actorSymbol(_data) = ($targetType(input)*) realloc($actorSymbol(_data), $actorSymbol(length));

for ($actorClass(i) = 0; $actorClass(i) < $actorSymbol(length); $actorClass(i)++) {
	$actorSymbol(_data)[$actorClass(i)] = $convert_$elementType(taps)_$cgType(input)($actorSymbol(_zero));
}
$actorSymbol(_mostRecent) = $actorSymbol(_phaseLength);
/**/



/*** prefireBlock ***/
//$actorSymbol(_zero) = $zero_$cgType(input)();
//
//$actorSymbol(_phaseLength) = $actorSymbol(_taps).payload.$cgType(taps)->size / $val(interpolation);
//
//if (($actorSymbol(_taps).payload.$cgType(taps)->size % $val(interpolation)) != 0) {
//	$actorSymbol(_phaseLength)++;
//}
//
//// Create new data array and initialize index into it.
//// Avoid losing the data if possible.
//// NOTE: If the filter length increases, then it is impossible
//// to correctly initialize the delay line to contain previously
//// seen data, because that data has not been saved.
//$actorSymbol(newLength) = $actorSymbol(_phaseLength) + $val(decimation);
//
//if ($actorSymbol(newLength) != $actorSymbol(length)) {
//	$actorSymbol(_data) = ($targetType(input)*) realloc($actorSymbol(_data), $actorSymbol(newLength));
//
//	for ($actorClass(i) = $actorSymbol(length); $actorClass(i) < $actorSymbol(newLength); $actorClass(i)++) {
//		$actorSymbol(_data)[$actorClass(i)] = $convert_$elementType(taps)_$cgType(input)($actorSymbol(_zero));
//	}
//	$actorSymbol(length) = $actorSymbol(newLength);
//	$actorSymbol(_mostRecent) = $actorSymbol(_phaseLength);
//}
/**/




/*** fireBlock0 ***/
$actorClass(bufferIndex) = 0;
$actorClass(inputIndex) = 0;
/**/

/*** fireBlock ***/
// Phase keeps track of which phase of the filter coefficients
// are used. Starting phase depends on the $val(decimationPhase) value.
$actorClass(phase) = $val(decimation) - $val(decimationPhase) - 1;

// Transfer decimation inputs to _data[]
for ($actorClass(inC) = 1; $actorClass(inC) <= $val(decimation); $actorClass(inC)++) {
	if (--$actorSymbol(_mostRecent) < 0) {
		$actorSymbol(_mostRecent) = $actorSymbol(length) - 1;
	}

	//_data[_mostRecent] = input.get(0);
	$actorSymbol(_data)[$actorSymbol(_mostRecent)] = $ref(input, $actorClass(inputIndex)++);
}

//printf("%s\n", $toString_DoubleArray($actorSymbol(_data)[0]));
//printf("%s\n", $toString_DoubleArray($actorSymbol(_data)[1]));
//printf("%s\n", $toString_DoubleArray($actorSymbol(_data)[2]));
//printf("%s\n", $toString_DoubleArray($actorSymbol(_data)[3]));
//printf("%s\n", $toString_DoubleArray($actorSymbol(_data)[4]));
//printf("%s\n", $toString_DoubleArray($actorSymbol(_data)[5]));
//printf("%s\n", $toString_DoubleArray($actorSymbol(_data)[6]));

// Interpolate once for each input consumed
for ($actorClass(inC) = 1; $actorClass(inC) <= $val(decimation); $actorClass(inC)++) {
	// Produce however many outputs are required
	// for each input consumed
	while ($actorClass(phase) < $val(interpolation)) {
		$actorSymbol(_outToken) = $convert_$elementType(taps)_$cgType(output)($actorSymbol(_zero));

		// Compute the inner product.
		for ($actorClass(i) = 0; $actorClass(i) < $actorSymbol(_phaseLength); $actorClass(i)++) {
			$actorClass(tapsIndex) = ($actorClass(i) * $val(interpolation)) + $actorClass(phase);

			$actorClass(dataIndex) = (($actorSymbol(_mostRecent) + $val(decimation)) - $actorClass(inC) + $actorClass(i)) % ($actorSymbol(length));

			if ($actorClass(tapsIndex) < $actorSymbol(_taps).payload.$cgType(taps)->size) {
				$actorSymbol(_tapItem) = $cgType(taps)_get($actorSymbol(_taps), $actorClass(tapsIndex));
				$actorSymbol(_dataItem) = $actorSymbol(_data)[$actorClass(dataIndex)];
				$actorSymbol(_dataItem) = $multiply_$elementType(taps)_$cgType(input)($actorSymbol(_tapItem), $actorSymbol(_dataItem));
				$actorSymbol(_outToken) = $add_$cgType(output)_$cgType(input)($actorSymbol(_outToken), $actorSymbol(_dataItem));
			}

			// else assume tap is zero, so do nothing.
		}

		$ref(output, ($actorClass(bufferIndex)++)) = $actorSymbol(_outToken);
		$actorClass(phase) += $val(decimation);
	}

	$actorClass(phase) -= $val(interpolation);
}
/**/

/*** wrapupBlock ***/
//$cgType(taps)_delete($actorSymbol(_data));
/**/
