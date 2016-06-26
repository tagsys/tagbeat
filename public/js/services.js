materialAdmin

    // =========================================================================
    // Utility service
    // =========================================================================
// =========================================================================
// Header Messages and Notifications list Data
// =========================================================================

    .service('messageService', ['$resource', function ($resource) {
        this.getMessage = function (img, user, text) {
            var gmList = $resource("data/messages-notifications.json");

            return gmList.get({
                img: img,
                user: user,
                text: text
            });
        }
    }])

    // =========================================================================
    // Malihu Scroll - Custom Scroll bars
    // =========================================================================
    .service('scrollService', function () {
        var ss = {};
        ss.malihuScroll = function scrollBar(selector, theme, mousewheelaxis) {
            $(selector).mCustomScrollbar({
                theme: theme,
                scrollInertia: 100,
                axis: 'yx',
                mouseWheel: {
                    enable: true,
                    axis: mousewheelaxis,
                    preventDefault: true
                }
            });
        }

        return ss;
    })

    .service('utilService', function ($http, $q) {
        var service = {};

        /**
         *  GET 方法
         * @param url
         *
         * @returns {*}
         */
        service.get = function (url) {

            var deferred = $q.defer();
            try {
                $http.get(url).success(function (data) {
                    if (data.errorCode == 0) {
                        deferred.resolve(data);
                    } else {
                        if (data.errorCode == 401) {
                            window.location.href = "/login";
                        } else {
                            deferred.reject(data);
                        }
                    }
                }).error(function (data) {
                    deferred.reject(data);
                });
            } catch (e) {
                deferred.reject({errorCode: -1, message: e.message});
            }
            return deferred.promise;
        };

        /**
         * POST 方法
         * @param url
         * @param postData
         * @returns {*}
         */
        service.post = function (url, data) {

            var deferred = $q.defer();
            try {
                $http.post(url, data).success(function (data) {
                    if (data.errorCode == 0) {
                        deferred.resolve(data);
                    } else {
                        if (data.errorCode == 401) {
                            window.location.href = "/login";
                        } else {
                            deferred.reject(data);
                        }
                    }
                }).error(function (data) {
                    deferred.reject(data);
                });
            } catch (e) {
                deferred.reject({errorCode: -1, message: e.message});
            }
            return deferred.promise;
        };

        return service;

    })


    // =========================================================================
    // Data service
    // EXP Structure:
    //    filters: {epc: {filtered, amount}}
    //    readings:[]
    //
    // =========================================================================
    .service('dataService', function (utilService, Loki, $timeout, $websocket, $location) {

        var service = {}

        service.tags = {};

        var dataStream = $websocket('ws://'+location.host+'/socket');

        dataStream.onMessage(function(message) {

            data = JSON.parse(message.data);

            console.log(data);

            if(data.errorCode==0 && data.type=='cr'){

                if(!service.tags[data.epc]) {
                    service.tags[data.epc]={ epc:data.epc, checked:false, originalSignal:[], recoveredSignal:[],spectrum: null}
                }

                service.tags[data.epc].originalSignal = data.originalSignal;
                service.tags[data.epc].recoveredSignal = data.recoveredSignal;
            }

        });

        dataStream.onError(function(message){
            sweetAlert("Oops...Something wrong!", "Websocket error....", "error");
        })


        return service;
    })
    .service('notifyService', function(){
        var gs = {};
        gs.notify = function(message, type) {
            $.notify({
                message: message
            },{
                type: type,
                allow_dismiss: false,
                label: 'Cancel',
                className: 'btn-xs btn-inverse',
                placement: {
                    from: 'top',
                    align: 'right'
                },
                delay: 2500,
                animate: {
                    enter: 'animated bounceIn',
                    exit: 'animated bounceOut'
                },
                offset: {
                    x: 20,
                    y: 85
                }
            });
        }

    return gs;
})


