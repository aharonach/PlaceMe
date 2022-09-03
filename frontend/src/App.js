import React, {createContext, useState} from "react";
import {Header, Footer, Page} from "./layouts";
import {Container} from "react-bootstrap";
import RecordContext from "./context/RecordContext";
import AppRoutes from "./AppRoutes";

function App() {
    const [record, setRecord] = useState({ record: null, displayFields: [] });

    return (
        <div className="App">
            <Header />
            <Container>
                <RecordContext.Provider value={{ record: record, setRecord: setRecord }}>
                    <AppRoutes />
                </RecordContext.Provider>
                <Footer />
            </Container>
        </div>
    );
}

export default App;
