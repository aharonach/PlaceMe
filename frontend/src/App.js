import React from "react";
import {Header, Footer} from "./layouts";
import {Col, Container, Row} from "react-bootstrap";
import AppRoutes from "./AppRoutes";
import './index.scss';

function App() {
    return (
        <Container fluid className="App px-4">
            <Row className="gx-5">
                <Col style={{paddingLeft:0}} id="sidebar" className="shadow">
                    <Header />
                    <Footer />
                </Col>
                <Col id="main">
                    <AppRoutes />
                </Col>
            </Row>
        </Container>
    );
}

export default App;
