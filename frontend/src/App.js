import React from "react";
import {Header, Footer} from "./layouts";
import {Col, Container, Row} from "react-bootstrap";
import AppRoutes from "./AppRoutes";
import './index.scss';

function App() {
    return (
        <Container fluid className="App px-4">
            <Row className="gx-5">
                <Col md={3} style={{paddingLeft:0}}>
                    <Header />
                </Col>
                <Col md={9} id="main">
                    <AppRoutes />
                    <Footer />
                </Col>
            </Row>
        </Container>
    );
}

export default App;
