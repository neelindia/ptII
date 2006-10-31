/***preinitBlock***/   
#ifndef _JAVA_INVOCATION_INTERFACE_PREINIT
#define _JAVA_INVOCATION_INTERFACE_PREINIT
    JavaVM* jvm;
    JNIEnv* env;
    JavaVMInitArgs args;
    JavaVMOption options[1];
#endif
    
    jclass $actorSymbol(frameClass);
    jobject $actorSymbol(frameObject);
    jmethodID $actorSymbol(frameConstructor);
    jmethodID $actorSymbol(frameGetValue);
/**/

/***initBlock***/
#ifndef _JAVA_INVOCATION_INTERFACE_INIT
#define _JAVA_INVOCATION_INTERFACE_INIT
    args.version = JNI_VERSION_1_4;
    args.nOptions = 1;
	options[0].optionString = "-Djava.class.path=c:/ptII";
	args.options = options;
	args.ignoreUnrecognized = JNI_FALSE;

    JNI_CreateJavaVM(&jvm, (void **)&env, &args);
#endif

    $actorSymbol(frameClass) = (*env)->FindClass
            (env, "ptolemy/actor/lib/gui/SliderSource$SliderFrame"); 
    $actorSymbol(frameConstructor) = (*env)->GetMethodID
            (env, $actorSymbol(frameClass), "<init>", "(IIIILjava/lang/String;)V");
    $actorSymbol(frameObject) = (*env)->NewObject
            (env, $actorSymbol(frameClass), $actorSymbol(frameConstructor), 
            $val(minimum), $val(maximum), 
            $val(majorTickSpacing), $val(minorTickSpacing), 
            (*env)->NewStringUTF(env, "$val(title)"));
    $actorSymbol(frameGetValue) = (*env)->GetMethodID
            (env, $actorSymbol(frameClass), "getValue", "()I");    
/**/

/***fireBlock***/
    $ref(output) = (*env)->CallIntMethod
            (env, $actorSymbol(frameObject), $actorSymbol(frameGetValue)); 
/**/
