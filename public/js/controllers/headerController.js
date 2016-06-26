/**
 * Created by Young on 16/6/24.
 */
materialAdmin
.controller('headerController', function($scope, $timeout, messageService){

    // Clear Local Storage
    $scope.about = function() {

        //Get confirmation, if confirmed clear the localStorage
        swal({
            imageUrl: "img/logo.png",
            title: "About Tagbeat <br/> Sensing mechanical vibration using backscatter signal.",
            text: "Version: 1.0 <br/><br/> Developer: Lei Yang, Qiongzheng Lin <br/><br/> License: MIT License",
            html: true,
            closeOnConfirm: false
        });

    }

})