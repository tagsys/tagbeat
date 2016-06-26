// =========================================================================
//  dashborad
// =========================================================================
materialAdmin
.controller('homeController', function ($scope, $state, utilService, dataService,notifyService,highchartsNG) {


    $scope.tagseeServer = "http://127.0.0.1:9092";

    $scope.reader = "192.168.1.213";

    $scope.tags = dataService.tags;

    $scope.isReading = false;

    $scope.playMode = false;

    $scope.history = [];

    $scope.params = {
        N: 5000,
        Q: 5,
        K: 5
    }

    $scope.$watch(function(){
        return dataService.tags;
    }, function(current, previous){
        $scope.tags = current;
        console.log($scope.tags);
    })

    $scope.$watch('params.N', function(newValue, oldValue){
        if(newValue != oldValue) {
            utilService.get("/changeParam?N=" + $scope.params.N).then(function (result) {
                notifyService.notify("Succeeded set the parameter of N.", "info");
            }, function (error) {
                notifyService.notify("Failed to set the parameter of N.", "danger");
            });
        }
    })

    $scope.$watch('params.Q', function(newValue, oldValue){
        if(newValue != oldValue) {
            utilService.get("/changeParam?Q=" + $scope.params.Q).then(function (result) {
                notifyService.notify("Succeeded set the parameter of Q.", "info");
            }, function (error) {
                notifyService.notify("Failed to set the parameter of Q.", "danger");
            });
        }
    })

    $scope.$watch('params.K', function(newValue, oldValue){
        if(newValue!=oldValue) {
            utilService.get("/changeParam?K=" + $scope.params.K).then(function (result) {
                notifyService.notify("Succeeded to set the parameter of K.", "info");
            }, function (error) {
                notifyService.notify("Failed to set the parameter of K.", "danger");
            });
        }
    })
    

    $scope.togglePlay = function(){

        $scope.playMode = !$scope.playMode;

        utilService.get("/history").then(function(result){

            console.log(result);
            $scope.history = result.history;

        },function(){
            notifyService.notify("Failed to obtain history.", "danger");
        })

    }

    $scope.play = function(filename){

        utilService.get("/replay?filename="+filename).then(function(){
            notifyService.notify("Succeeded to replay ("+filename+")", "info");
        },function(){
            notifyService.notify("Failed to replay ("+filename+")", "danger");
        })

    }


    $scope.start = function(){

        utilService.get($scope.tagseeServer+"/service/agent/"+$scope.reader+"/start").then(function(){

            $scope.isReading = true;

            notifyService.notify("Successfully start the reader.", "info");

        },function(message){


            sweetAlert("Oops....", "Something went wrong!", "error");

        })

    }

    $scope.stop = function(){

        utilService.get($scope.tagseeServer+"/service/agent/"+$scope.reader+"/stop").then(function(){
            $scope.isReading = false;
            notifyService.notify("Successfully stop the reader.", "info");
        },function(message){
            notifyService.notify("Successfully stop the reader.", "danger");
        })
    }
    
    $scope.check = function(value){

        var filters = {};
        Object.keys($scope.tags).forEach(function (key) {
           filters[key] = $scope.tags[key].checked;
        })

        utilService.post("/filtering", filters).then(function(){
            notifyService.notify("Succeeded to set filters.", "info");
        },function(){
            notifyService.notify("Failed to set filters.", "danger");
        });

    }

    $scope.refreshFilters = function(){


    }


    var updatePhaseSeries = function() {

        var series = $scope.phaseChartConfig.series;

        Object.keys($scope.tags).forEach(function(epc){

            var value = $scope.tags[epc];

            var foundOriginal = false;
            var foundRecovered = false;
            for(var i=0;i<series.length;i++){
                if(series[i].name==epc+"-o"){
                    series[i].data = value.originalSignal;
                    foundOriginal = true;
                }
                if(series[i].name==epc+"-r"){
                    series[i].data = value.recoveredSignal;
                    foundRecovered = true;
                }
            }

            if(!foundRecovered){
                series.push({
                    name:epc+"-r",
                    data:value.recoveredSignal
                })
            }

            if(!foundOriginal){
                series.push({
                    name:epc+"-o",
                    data:value.originalSignal
                })
            }




        })

    }

    var updateCharts = function(){

        updatePhaseSeries();

    }

    highchartsNG.ready(function(){

        $scope.phaseChartConfig = {
            options:{
                chart: {
                    zoomType: 'x'
                },
                title: {
                    text: ''
                },
                xAxis: {
                    type: 'linear'
                },
                yAxis: {
                    title: {
                        text: 'Radians'
                    }
                },
                legend: {
                    enabled: true
                },
                plotOptions:{
                    marker:{
                        radius:5
                    }
                }
            },
            series: []
        }

        /**
         * Refreshing the three charts regularly.
         */
        setInterval(function(){
            updateCharts();
        },1000);

    },this);


})
