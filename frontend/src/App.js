import React, {createContext, useState} from "react";
import {Header, Footer, Page} from "./layouts";
import {Col, Container, Row} from "react-bootstrap";
import RecordContext from "./context/RecordContext";
import AppRoutes from "./AppRoutes";
import './index.scss';

function App() {
    const [record, setRecord] = useState({ record: null, displayFields: [] });

    return (
        <Container fluid className="App">
            <Row>
                <Col md={3}><Header /></Col>
                <Col md={9}>
                    <RecordContext.Provider value={{ record: record, setRecord: setRecord }}>
                        <AppRoutes />
                    </RecordContext.Provider>
                    <Footer />
                </Col>
            </Row>
        </Container>
    );
}

export default App;
