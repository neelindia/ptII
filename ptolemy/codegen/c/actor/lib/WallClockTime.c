/***preinitBlock***/
    time_t $actorSymbol(startTime);
/**/

/***initBlock***/
    $actorSymbol(startTime) = time((time_t*) NULL);
/**/

/***transferBlock($channel)***/
    $ref(output) = time((time_t*) NULL) - $actorSymbol(startTime);
    $ref(passthrough#$channel) = $ref(trigger#$channel);
/**/

