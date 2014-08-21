(ns puppetlabs.services.master.master-core-test
  (:require [clojure.test :refer :all]
            [puppetlabs.services.master.master-core :refer :all]
            [ring.mock.request :as mock]))

(deftest test-master-routes
  (let [handler     (fn ([req] {:request req})
                        ([env req] {:request req :environment env}))
        app         (compojure-app handler)
        request     (fn r ([path] (r :get path))
                          ([method path] (app (mock/request method path))))]
    (is (nil? (request "/v2.0/foo")))
    (is (= 200 (:status (request "/v2.0/environments"))))
    (is (nil? (request "/foo")))
    (is (nil? (request "/foo/bar")))
    (doseq [[method paths]
            {:get ["catalog"
                   "node"
                   "facts"
                   "file_content"
                   "file_metadatas"
                   "file_metadata"
                   "resource_type"]
             :post ["catalog"]
             :put ["report"]}
            path paths]
      (let [resp (request method (str "/foo/" path "/bar"))]
        (is (= 200 (:status resp))
            (str "Did not get 200 for method: "
                 method
                 ", path: "
                 path))
        (is (= "foo" (:environment resp))
            (str "Did not get expected response for method: "
                 method
                 ", path: "
                 path))))))