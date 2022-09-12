import {Alert, Tab, Tabs} from "react-bootstrap";
import useFetchList from "../../hooks/useFetchList";
import Loading from "../Loading";
import ConfigPage from "./ConfigPage";

export default function ConfigsList() {
    const [configs, error, loading] = useFetchList({
        fetchUrl: '/placements/configs',
        propertyName: 'placeEngineConfigList'
    });

    return (
        <>
            <div className="page-header">
                <h1>Evolutionary Algorithm Configurations</h1>
            </div>
            <Loading show={loading} />
            {error && <Alert variant="danger">{error}</Alert>}
            {!error && configs && (
                <Tabs defaultActiveKey="config-1" className="mb-3">
                    {configs.map( config => (
                        <Tab key={config.id} eventKey={`config-${config.id}`} title={config.name}>
                            <ConfigPage configId={config.id} />
                        </Tab>
                    ))}
                </Tabs>
            )}
        </>
    );
}