modules = {
    application {
        dependsOn 'jquery'
        resource url: 'js/angular/angular.js'
        resource url: 'js/angular/angular-resource.js'
        resource url: 'js/angular/angular-cookies.min.js'
        resource url: 'js/angular/twitter-bootstrap.js'
        resource url: 'css/bootstrap.css'
    }

    serviceResource{
        resource url: 'js/services.js'
    }

    pieChartResources{
        resource url:'js/piechartJs/jsapi.js'
        resource url:'js/piechartJs/visualization_coherent.js'
        resource url:'js/piechartJs/visualization.js'
    }

    statusResource {
        dependsOn('application','pieChartResources','serviceResource')
        resource url: 'js/status.js'
    }

}