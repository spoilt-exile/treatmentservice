## List of possible improvements:
 1. Rework trigger, add logic to re-run job if errors were throwned during prevuious execution (`lastActualExecution()` and `lastCompletion()` differs);
 1. Make `getPlansForTaskGeneration()` to return only ids and with pagination (prevent high memory usage);
 1. Mark in db treatment plans which failed to generate tasks. Trigger logic should account for that by looking in db. Pros: more robust error handling. Cons: more complex trigger logic, additional request to db to find count of plans with such error;
 1. Maybe refactor recurrence pattern mechanism to make it simplier for front-end or mobile devs (not all of them knows RRULE and iCal). For example enum and integer;
 1. **`[OUT_OF_SCOPE]`** Instead of `generatedTill` in plans use `nextGeneration` with exact datetime when next task should be generated. Pros: less hassle with plans which generating tasks on rare ocassion, doesn't require to rewrite generated tasks on sudden changes in schedule. Cons: on inserting plan it requires to compute ahead a new date of generation, also new date should be in foreseeble future (1 year for example);
 1. **`[OUT_OF_SCOPE]`** Use message bus (any kind with retry and error handling) to generate tasks. Job still finds ids of plans to generate and emits it in bus. Message handlers generating tasks. Bus controls error handling and retries;
 1. **`[OUT_OF_SCOPE]`** Use Quartz?
 1. **`[OUT_OF_SCOPE]`** With certain limitation on search and browsing it's possible to avoid generation of tasks at all. All reqeuests should include datetime frame of a search and personal scope (doctor, nurse, patient) and plans within that scope will generate tasks within datetime frame on the fly. Insertion of the tasks may be done only upon completion (as log);
