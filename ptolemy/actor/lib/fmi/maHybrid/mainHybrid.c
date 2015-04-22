/* Generated from ptolemy/cg/kernel/generic/program/procedural/fmima/FMIMACodeGenerator.java _generateCode */
/* Generated by Ptolemy II (http://ptolemy.eecs.berkeley.edu)
Copyright (c) 2005-2014 The Regents of the University of California.
All rights reserved.
Permission is hereby granted, without written agreement and without
license or royalty fees, to use, copy, modify, and distribute this
software and its documentation for any purpose, provided that the above
copyright notice and the following two paragraphs appear in all copies
of this software.
IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
ENHANCEMENTS, OR MODIFICATIONS.
*/
/* end includeecode */
/* end typeResolution code */
/* end shared code */
/* end variable declaration code */
#include <stdio.h>
/* Check for __linux__ after including stdio.h, otherwise it is sometimes not defined. */
#ifdef __linux__
/* Needed for strdup and mkdtemp under RHEL 6.1 */
#define __USE_BSD
/* Needed for strdup and mkdtemp under Gentoo.
* see http://polarhome.com/service/man/?qf=STRDUP&af=0&tf=2&of=Gentoo
* If you change this file, then please change
* ptolemy/actor/lib/fmi/ma2/shared/sim_support.c
* On 01/27/2015 Marten wrote: Doubtful whether this is still true after
* the -std=gnu99 flag was turned on. Moreover, glibc 2.20, the
* _BSD_SOURCE macro is deprecated, see:
* http://man7.org/linux/man-pages/man7/feature_test_macros.7.html.
*/
#define _BSD_SOURCE
#endif
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <stdbool.h>
#include "fmi2.h"
#include "sim_support.h"
fmi2Integer tEnd = 10;
fmi2Integer tStart = 0;
#define fmuA20ptInt 0
#define NUMBER_OF_FMUS 1
#define NUMBER_OF_EDGES 0
#define MODEL_NAME "emsoft2013Hybrid"
const char* NAMES_OF_FMUS[] = {"fmuA20ptInt"};
int static i = 0;
static void setupParameters(FMU *fmu) {
    fmi2Status fmi2Flag = fmu->enterInitializationMode(fmu->component);
    if (fmi2Flag > fmi2Warning) {
        error("could not initialize model; failed FMI enter initialization mode");
    }
    printf("initialization mode entered\n");
    int _vr = 0;
    switch(i) {
        case(0): {
            break;
        }
    }
    i++;
    fmi2Flag = fmu->exitInitializationMode(fmu->component);
    printf("successfully initialized.\n");
    if (fmi2Flag > fmi2Warning) {
        error("could not initialize model; failed FMI exit initialization mode");
    }
}
static fmi2Component initializeFMU(FMU *fmu, fmi2Boolean visible,
fmi2Boolean loggingOn, int nCategories, char ** categories, char* name) {
    fmi2Status fmi2Flag;                     // return code of the fmu functions
    // instantiate the fmu
    fmu->callbacks.logger = fmuLogger;
    fmu->callbacks.allocateMemory = calloc;
    fmu->callbacks.freeMemory = free;
    fmu->callbacks.stepFinished = NULL; // fmi2DoStep has to be carried out synchronously
    fmu->callbacks.componentEnvironment = fmu; // pointer to current fmu from the environment.
    fmi2Real tolerance = 0;                 // used in setting up the experiment
    fmi2Boolean toleranceDefined = fmi2False; // true if model description define tolerance
    ValueStatus vs = valueIllegal;
    // handle to the parsed XML file
    ModelDescription* md = fmu->modelDescription;
    // global unique id of the fmu
    const char *guid = getAttributeValue((Element *) md, att_guid);
    // check for ability to get and set state
    fmu->canGetAndSetFMUstate = getAttributeBool((Element*) getCoSimulation(md),
    att_canGetAndSetFMUstate, &vs);
    fmu->canGetMaxStepSize = getAttributeBool((Element*) getCoSimulation(md),
    att_canGetMaxStepSize, &vs);
    // instance name
    const char *instanceName = getAttributeValue(
    (Element *) getCoSimulation(md), att_modelIdentifier);
    // path to the fmu resources as URL, "file://C:\QTronic\sales"
    char *fmuResourceLocation = getTempResourcesLocation(); // TODO: returns crap. got to save the location for every FMU somehow.
    fmu->handleIntegerTime = (fmi2Boolean) getAttributeValue((Element *) getCoSimulation(md), att_canHandleIntegerTime);
    fmi2Component comp = fmu->instantiate(instanceName, fmi2CoSimulation, guid,
    fmuResourceLocation, &fmu->callbacks, visible, loggingOn);
    printf("instance name: %s, \nguid: %s, \nressourceLocation: %s\n",
    instanceName, guid, fmuResourceLocation);
    free(fmuResourceLocation);
    if (!comp) {
        printf("Could not initialize model with guid: %s\n", guid);
        return NULL;
    }
    Element *defaultExp = getDefaultExperiment(fmu->modelDescription);
    if (defaultExp) {
        tolerance = getAttributeDouble(defaultExp, att_tolerance, &vs);
    }
    if (vs == valueDefined) {
        toleranceDefined = fmi2True;
    }
    if (nCategories > 0) {
        fmi2Flag = fmu->setDebugLogging(comp, fmi2True, nCategories,
        (const fmi2String*) categories);
        if (fmi2Flag > fmi2Warning) {
            error("could not initialize model; failed FMI set debug logging");
            return NULL;
        }
    }
    fmi2Flag = fmu->setupExperiment(comp, toleranceDefined, tolerance, tStart,
    fmi2True, tEnd);
    if (fmi2Flag > fmi2Warning) {
        error("could not initialize model; failed FMI setup experiment");
        return NULL;
    }
    return comp;
}
static fmi2Status checkForLegacyFMUs(FMU* fmus, bool *isLegacyFmu, int*legacyFmuIndex) {
    printf("Checking for legacy FMUs!\n");
    int legacyFmus = 0;
    for (int i = 0; i < NUMBER_OF_FMUS; i++) {
        if (!fmus[i].canGetAndSetFMUstate) {
            legacyFmus++;
            *legacyFmuIndex = i;
            *isLegacyFmu = true;
            if (legacyFmus > 1) {
                printf("More than one legacy FMU detected. The system cannot be simulated.\n");
                return fmi2Error;
            }
        }
    }
    return fmi2OK;
}
static fmi2Status setValue(portConnection* connection) {
    fmi2Status fmi2Flag;
    fmi2Integer tempInt;
    fmi2Real tempReal;
    fmi2Boolean tempBoolean;
    fmi2String tempString;
    // get source value and cast if necessary
    switch (connection->sourceType) {
        case fmi2_Integer:
        fmi2Flag = connection->sourceFMU->getInteger(connection->sourceFMU->component, &connection->sourcePort, 1, &tempInt);
        tempReal = (fmi2Real) tempInt;
        tempBoolean = (tempInt == 0 ? fmi2False : fmi2True);
        break;
        case fmi2_Real:
        fmi2Flag = connection->sourceFMU->getReal(connection->sourceFMU->component, &connection->sourcePort, 1, &tempReal);
        tempInt = (fmi2Integer) round(tempReal);
        tempBoolean = (tempReal == 0.0 ? fmi2False : fmi2True);
        break;
        case fmi2_Boolean:
        fmi2Flag = connection->sourceFMU->getBoolean(connection->sourceFMU->component, &connection->sourcePort, 1, &tempBoolean);
        tempInt = (fmi2Integer) tempBoolean;
        tempReal = (fmi2Real) tempBoolean;
        break;
        case fmi2_String:
        fmi2Flag = connection->sourceFMU->getString(connection->sourceFMU->component, &connection->sourcePort, 1, &tempString);
        break;
        default:
        return fmi2Error;
    }
    if (fmi2Flag > fmi2Warning) {
        printf("Getting the value of an FMU caused an error.");
        return fmi2Flag;
    }
    if (connection->sourceType != connection->sinkType && (connection->sinkType == fmi2_String || connection->sourceType == fmi2_String)) {
        printf("A connection of FMUs has incompatible data types. Terminating simulation.\n");
        return fmi2Error;
    }
    // set sink value
    switch (connection->sinkType) {
        case fmi2_Integer:
        fmi2Flag = connection->sinkFMU->setInteger(connection->sinkFMU->component, &connection->sinkPort, 1, &tempInt);
        break;
        case fmi2_Real:
        fmi2Flag = connection->sinkFMU->setReal(connection->sinkFMU->component, &connection->sinkPort, 1, &tempReal);
        break;
        case fmi2_Boolean:
        fmi2Flag = connection->sinkFMU->setBoolean(connection->sinkFMU->component, &connection->sinkPort, 1, &tempBoolean);
        break;
        case fmi2_String:
        fmi2Flag = connection->sinkFMU->setString(connection->sinkFMU->component, &connection->sinkPort, 1, &tempString);
        break;
        default:
        return fmi2Error;
    }
    return fmi2Flag;
}
void terminateSimulation(FMU *fmus, int returnValue, FILE* file, fmi2Integer stepSize,
int nSteps) {
    const char* STATUS[] = { "fmi2OK", "fmi2Warning", "fmi2Discard",
    "fmi2Error", "fmi2Fatal", "fmi2Pending" };
    for (int i = 0; i < NUMBER_OF_FMUS; i++) {
        fmus[i].terminate(fmus[i].component);
        if (fmus[i].lastFMUstate != NULL) {
            fmi2Status status = fmus[i].freeFMUstate(fmus[i].component,
            &fmus[i].lastFMUstate);
            printf("Terminating with status: %s\n", STATUS[status]);
        }
        fmus[i].freeInstance(fmus[i].component);
    }
    // print simulation summary
    if (returnValue == 1) {
        printf("Simulation from %d to %d terminated successful\n", tStart,
        tEnd);
    } else {
        printf("Simulation from %d to %d terminated early!\n", tStart, tEnd);
    }
    printf("  steps ............ %d\n", nSteps);
    printf("  fixed step size .. %d\n", stepSize);
    fclose(file);
    return;
}
static fmi2Status rollbackFMUs(FMU *fmus) {
    for (int i = 0; i < NUMBER_OF_FMUS; i++) {
        if (fmus[i].canGetAndSetFMUstate && !fmus[i].canGetMaxStepSize) {
            fmi2Status localStatus = fmus[i].setFMUstate(fmus[i].component, fmus[i].lastFMUstate);
            if (localStatus > fmi2Warning) {
                printf("Rolling back FMU (%s) failed!\n", NAMES_OF_FMUS[i]);
                return localStatus;
            }
        }
    }
    return fmi2OK;
}
// Determine the minimum resolution among the User-Defined and the resolution required by the FMUs
// Then, notify the FMUs with integer representation of time about the resolution to adopt.
// It also return the scaleFactor for each FMU, in order to perform conversion between
// Integer Time and Double for those FMUs that do not support Integer Time.
void setTimingResolutions(FMU *fmus, fmi2Integer *resolution, fmi2Integer *scaleFactor) {
    // Determine the minimum resolution
    for (int i = 0; i < NUMBER_OF_FMUS; i++) {
        FMU fmu = fmus[i];
        if (fmu.handleIntegerTime) {
            fmi2Integer precision;
            fmi2Status currentStatus = fmu.getTimePrecision(fmu.component, &precision);
            *resolution = min(*resolution, precision);
        }
    }
    // Set the resolution
    for (int i = 0; i < NUMBER_OF_FMUS; i++) {
        FMU fmu = fmus[i];
        if (fmu.handleIntegerTime) {
            fmi2Integer precision;
            fmi2Status currentStatus = fmu.getTimePrecision(fmu.component, &precision);
            scaleFactor[i] = (fmi2Integer) pow(10,  precision - *resolution);
            currentStatus = fmu.setTimePrecision(fmu.component, precision);
        }
        else {
            scaleFactor[i] = (fmi2Integer) pow(10, - *resolution);
        }
    }
}
fmi2Status getMaxStepSize(FMU *fmu, fmi2Integer scaleFactor, fmi2Integer *maxStepSize) {
    fmi2Status currentStatus = fmi2OK;
    if ((*fmu).handleIntegerTime) {
        fmi2Integer tmp;
        currentStatus = (*fmu).getHybridMaxStepSize((*fmu).component, &tmp);
        *maxStepSize = tmp;;
    }
    else {
        fmi2Real maxStepSize_tmp;
        currentStatus = (*fmu).getMaxStepSize((*fmu).component, &maxStepSize_tmp);
        maxStepSize_tmp = maxStepSize_tmp * scaleFactor;
        *maxStepSize = (fmi2Integer) maxStepSize_tmp;
    }
    return currentStatus;
}
fmi2Status doStep(FMU *fmu, fmi2Integer time, fmi2Integer stepSize, fmi2Boolean noSetFMUStatePriorToCurrentPoint, fmi2Integer scaleFactor) {
    fmi2Status currentStatus = fmi2OK;
    if ((*fmu).handleIntegerTime) {
        currentStatus = (*fmu).doHybridStep((*fmu).component, time, stepSize, noSetFMUStatePriorToCurrentPoint);
    }
    else {
        fmi2Real time_tmp = (time / 1.0) / scaleFactor;
        fmi2Real stepSize_tmp = (fmi2Real) (stepSize / 1.0);
        stepSize_tmp = stepSize_tmp / scaleFactor;
        currentStatus = (*fmu).doStep((*fmu).component, time_tmp, stepSize_tmp, noSetFMUStatePriorToCurrentPoint);
    }
    return currentStatus;
}
fmi2Status getRealStatus(FMU *fmu, fmi2StatusKind fmi2LastSuccessfulTime, fmi2Integer *lastSuccessfulTime, fmi2Integer scaleFactor) {
    fmi2Status currentStatus = fmi2OK;
    if ((*fmu).handleIntegerTime) {
        currentStatus = fmu->getRealStatus((*fmu).component, fmi2LastSuccessfulTime, lastSuccessfulTime);
        *lastSuccessfulTime = *lastSuccessfulTime * scaleFactor;
    }
    else {
        fmi2Real lastSuccessfulTime_tmp;
        currentStatus = (*fmu).getRealStatus(fmu->component, fmi2LastSuccessfulTime, &lastSuccessfulTime_tmp);
        lastSuccessfulTime_tmp = lastSuccessfulTime_tmp * scaleFactor;
        *lastSuccessfulTime = (fmi2Integer) lastSuccessfulTime_tmp;
    }
    return currentStatus;
}
// simulate the given FMUs from tStart = 0 to tEnd.
static int simulate(FMU *fmus, portConnection* connections, fmi2Integer requiredResolution,
fmi2Boolean loggingOn, char separator) {
    // Simulation variables
    fmi2Integer time = 0;
    fmi2Integer nSteps = 0;
    fmi2Integer stepSize = 10;
    fmi2Integer resolution = requiredResolution;
    fmi2Integer scaleFactor[NUMBER_OF_FMUS];
    fmi2Integer localTime[NUMBER_OF_FMUS];
    FILE* file;
    int legacyFmuIndex = 0;
    bool isLegacyFmu = false;
    // Determine the simulation resolution
    setTimingResolutions(fmus, &resolution, &scaleFactor);
    // Open result file
    if (!(file = fopen(RESULT_FILE, "w"))) {
        printf("could not write %s because:\n", RESULT_FILE);
        printf("    %s\n", strerror(errno));
        return 0;
    }
    // Check for legacy FMUs
    if (checkForLegacyFMUs(fmus, &isLegacyFmu, &legacyFmuIndex) > fmi2Warning) {
        terminateSimulation(fmus, 0, file, stepSize, nSteps);
        return 0;
    }
    for (int i = 0; i < NUMBER_OF_FMUS; i++) {
        setupParameters(&fmus[i]);
    }
    for (int i = 0; i < NUMBER_OF_FMUS; i++) {
        localTime[i] = 0;
    }
    // output solution for time t0
    outputRow(fmus, NUMBER_OF_FMUS, NAMES_OF_FMUS, time, 0, file, separator, TRUE);
    outputRow(fmus, NUMBER_OF_FMUS, NAMES_OF_FMUS, time, 0, file, separator, FALSE);
    // Simulation loop
    while (time < tEnd) {
        // Set connection values
        for (int i = 0; i < NUMBER_OF_EDGES; i++) {
            setValue(&connections[i]);
        }
        // Compute the maximum step size
        // (I) Predictable FMUs
        for (int i = 0; i < NUMBER_OF_FMUS; i++) {
            if (fmus[i].canGetMaxStepSize) {
                fmi2Integer maxStepSize;
                fmi2Status currentStatus = getMaxStepSize(&(fmus[i]), scaleFactor[i], &maxStepSize);
                if (currentStatus > fmi2Warning) {
                    printf("Could get the MaxStepSize for FMU (%s). Terminating simulation.\n", NAMES_OF_FMUS[i]);
                    terminateSimulation(fmus, 0, file, stepSize, nSteps);
                    return 0;
                }
                stepSize = min(stepSize, maxStepSize);
            }
        }
        // Compute the maximum step size
        // (II) Rollback FMUs
        for (int i = 0; i < NUMBER_OF_FMUS; i++) {
            if (fmus[i].canGetAndSetFMUstate && !fmus[i].canGetMaxStepSize) {
                fmi2Integer maxStepSize;
                fmi2Status currentStatus = fmus[i].getFMUstate(fmus[i].component, &fmus[i].lastFMUstate);
                if (currentStatus > fmi2Warning) {
                    printf("Saving state of FMU (%s) failed. Terminating simulation. Terminating simulation.\n", NAMES_OF_FMUS[i]);
                    terminateSimulation(fmus, 0, file, stepSize, nSteps);
                    return 0;
                }
                currentStatus = doStep(&fmus[i], time, stepSize, fmi2False, scaleFactor[i]);
                if (currentStatus > fmi2Discard) {
                    printf("Could not step FMU (%s) while determining the step size. Terminating simulation.\n", NAMES_OF_FMUS[i]);
                    terminateSimulation(fmus, 0, file, stepSize, nSteps);
                    return 0;
                }
                fmi2Integer lastSuccessfulTime;
                currentStatus = getRealStatus(&fmus[i], fmi2LastSuccessfulTime, &lastSuccessfulTime, scaleFactor[i]);
                if (currentStatus > fmi2Warning) {
                    printf("Could not get the last successful time instant for FMU (%s). Terminating simulation.\n", NAMES_OF_FMUS[i]);
                    terminateSimulation(fmus, 0, file, stepSize, nSteps);
                    return 0;
                }
                maxStepSize = lastSuccessfulTime - time;
                if (maxStepSize != 0) {
                    stepSize = min(stepSize, maxStepSize);
                }
            }
        }
        // Compute the maximum step size
        // (III) Legacy FMUs
        if (isLegacyFmu) {
            fmi2Integer maxStepSize;
            fmi2Status currentStatus = doStep(&fmus[legacyFmuIndex], time, stepSize, fmi2False, scaleFactor[legacyFmuIndex]);
            if (currentStatus > fmi2Warning) {
                printf("Could not step FMU (%s) [Legacy FMU]. Terminating simulation.\n", NAMES_OF_FMUS[legacyFmuIndex]);
                terminateSimulation(fmus, 0, file, stepSize, nSteps);
                return 0;
            }
            fmi2Integer lastSuccessfulTime;
            currentStatus = getRealStatus(&fmus[legacyFmuIndex], fmi2LastSuccessfulTime, &lastSuccessfulTime, scaleFactor[legacyFmuIndex]);
            if (currentStatus > fmi2Warning) {
                printf("Could get the last successful time instant for FMU (%s). Terminating simulation.\n", NAMES_OF_FMUS[legacyFmuIndex]);
                terminateSimulation(fmus, 0, file, stepSize, nSteps);
                return 0;
            }
            maxStepSize = lastSuccessfulTime - time;
            if (maxStepSize == 0) {
                stepSize = min(stepSize, maxStepSize);
            }
        }
        // Rolling back FMUs of type (II)
        {
            fmi2Status currentStatus = rollbackFMUs(fmus);
            if (currentStatus > fmi2Warning) {
                printf("Error while rolling back FMUs. Terminating simulation.");
                terminateSimulation(fmus, 0, file, stepSize, nSteps);
                return 0;
            }
        }
        // Perform doStep() for all FMUs with the discovered stepSize
        for (int i = 0; i < NUMBER_OF_FMUS; i++) {
            if (fmus[i].canGetMaxStepSize || fmus[i].canGetAndSetFMUstate) {
                fmi2Status currentStatus = doStep(&fmus[i], time, stepSize, fmi2True, scaleFactor[i]);
                if (currentStatus > fmi2Warning) {
                    printf("Could not step FMU (%s) after minimum step has been determined. Terminating simulation.\n", NAMES_OF_FMUS[i]);
                    terminateSimulation(fmus, 0, file, stepSize, nSteps);
                    return 0;
                }
            }
        }
        time += stepSize;
        outputRow(fmus, NUMBER_OF_FMUS, NAMES_OF_FMUS, time, resolution, file, separator,FALSE);
        nSteps++;
    }
    terminateSimulation(fmus, 1, file, stepSize, nSteps);
    return 1;
}
/* end preinitialize code */
/* end preinitialize method code */
/* Before appending splitPreinitializeMethodBodyCode[0]. */
/* After appending splitPreinitializeMethodBodyCode[0]. */
/* preinitialization entry code */
/* preinitialization exit code */
/* Before appending splitVariableInitCode[0]. */
/*
After appending splitVariableInitCode[0].
*/
/* Before appending splitInitializeCode[0]. */
/* After appending splitInitializeCode[0]. */
/* Before appending initializeEntryCode */
/* initialization entry code */
/* After appending initializeEntryCode */
/* Before appending splitVariableInitCode[1]. */
/* After appending splitVariableInitCode[1]. */
/* Before appending splitInitializeCode[1]. */
/* After appending splitInitializeCode[1]. */
/* Before appending initializeExitCode. */
/* initialization exit code */
/* wrapup entry code */
/* wrapup exit code */
/* ptolemy/cg/kernel/generic/program/procedural/fmima/FMIMACodeGenerator.java */
/* Probably the thing to do is to create .c files and copy them over to the cg/ directory. */
/* Then we can create a few functions that do the real work. */
int main(int argc, char *argv[]) {
    /* ptolemy/cg/adapter/generic/program/procedural/fmima/adapters/ptolemy/actor/TypedCompositeActor.java start
    emsoft2013Hybrid contains:  */
    /* ptolemy/cg/adapter/generic/program/procedural/fmima/adapters/ptolemy/actor/Director.java start
    DE Director */
    #if WINDOWS
    const char* fmuFileNames[NUMBER_OF_FMUS];
    #else
    char* fmuFileNames[NUMBER_OF_FMUS];
    #endif
    int i;
    // parse command line arguments and load the FMU
    // default arguments value
    fmi2Integer requiredResolution = 0;
    int loggingOn = 0;
    char csv_separator = ',';
    char **categories = NULL;
    int nCategories = 0;
    fmi2Boolean visible = fmi2False;           // no simulator user interface
    // Create and allocate arrays for FMUs and port mapping
    FMU *fmus = calloc(NUMBER_OF_FMUS, sizeof(FMU));
    portConnection* connections = calloc(NUMBER_OF_EDGES, sizeof(portConnection));
    printf("-> Parsing arguments...\n");
    parseArguments(argc, argv, &tEnd, &requiredResolution, &loggingOn, &csv_separator, &nCategories, &categories);
    printf("Loading FMU fmuA20ptInt...\n");
    loadFMU(&fmus[fmuA20ptInt], "/Users/fabiocremona/Documents/SSSA-PhD/Libraries/workspacePt2/ptII/ptolemy/actor/lib/fmi/fmus/fmuA20ptInt/fmuA20ptInt.fmu");
    fmuFileNames[fmuA20ptInt] = strdup("/Users/fabiocremona/Documents/SSSA-PhD/Libraries/workspacePt2/ptII/ptolemy/actor/lib/fmi/fmus/fmuA20ptInt/fmuA20ptInt.fmu");
    printf("Initializing FMU fmuA20ptInt...\n");
    fmus[fmuA20ptInt].component = initializeFMU(&fmus[fmuA20ptInt], visible, loggingOn, nCategories, categories, NAMES_OF_FMUS[fmuA20ptInt]);
    // run the simulation
    printf("FMU Simulator: run '%s' from t=0..%d with step size h=%d, loggingOn=%d, csv separator='%c' ",
    MODEL_NAME, tEnd, requiredResolution, loggingOn, csv_separator);
    printf("log categories={ ");
        for (i = 0; i < nCategories; i++) {
            printf("%s ", categories[i]);
        }
    printf("}\n");
    simulate( fmus, connections, requiredResolution, loggingOn, csv_separator);
    printf("CSV file '%s' written\n", RESULT_FILE);
    // release FMUs
    #ifdef _MSC_VER
    for (i = 0; i < NUMBER_OF_FMUS; i++) {
        FreeLibrary(fmus[i]->dllHandle);
    }
    #else
    for (i = 0; i < NUMBER_OF_FMUS; i++) {
        dlclose(fmus[i].dllHandle);
    }
    #endif
    for (i = 0; i < NUMBER_OF_FMUS; i++) {
        freeModelDescription(fmus[i].modelDescription);
    }
    if (categories) {
        free(categories);
    }
    free( fmus);
    return EXIT_SUCCESS;
    /* ptolemy/cg/adapter/generic/program/procedural/fmima/adapters/ptolemy/actor/Director.java end */
    /* ptolemy/cg/adapter/generic/program/procedural/fmima/adapters/ptolemy/actor/TypedCompositeActor.java end */
    return 0;
}
/* Fire emsoft2013Hybrid_DE_Director */
/* closing exit code */
/* main exit code */
