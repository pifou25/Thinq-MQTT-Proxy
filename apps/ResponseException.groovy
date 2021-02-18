class ResponseException extends Exception {
    Response response

    ResponseException(Response response) {
        this.response = response
    }

    Response getResponse() {
        return response
    }
}
