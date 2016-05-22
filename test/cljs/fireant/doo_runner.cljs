(ns fireant.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [fireant.core-test]))

(doo-tests 'fireant.core-test)
