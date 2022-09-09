import {Outlet, useNavigate, useParams} from "react-router-dom";
import Loading from "../Loading";
import {Alert, Button, ButtonGroup, Modal, Stack} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";
import useFetchRecord from "../../hooks/useFetchRecord";
import {useState} from "react";
import Import from "./Import";
import {BASE_URL, CSV_CONTENT_TYPE} from "../../api";

export default function PlacementPage(){
    let { placementId } = useParams();
    const [placement, error, loading, axiosFetch, getPlacement] = useFetchRecord({
        fetchUrl: `/placements/${placementId}`,
        displayFields: [ 'name' ]
    });
    const [showImport, setShowImport] = useState(false);
    const exportDownloadUrl = `${BASE_URL}placements/${placementId}/export`;

    let navigate = useNavigate();

    const handleDelete = () => {
        axiosFetch({
            method: 'delete',
            url: `/placements/${placement.id}`,
        }).then(() => navigate('/placements', {replace: true}));
    }

    return (
        <>
            <Loading show={loading} />
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && placement &&
                <article>
                    <div className={"page-header"}>
                        <h1>{placement.name}</h1>
                        <Stack direction="horizontal" gap={2}>
                            <ButtonGroup>
                                <LinkContainer to={`/placements/${placement.id}/edit`}><Button>Edit Placement</Button></LinkContainer>
                                <LinkContainer to={`/placements/${placement.id}/results`}><Button>Show All Optional Results</Button></LinkContainer>
                                <Button variant="danger" onClick={handleDelete}>Delete Placement</Button>
                            </ButtonGroup>
                            <ButtonGroup>
                                <Button variant="secondary" onClick={() => setShowImport(true)}>Import</Button>
                                <Button as="a" variant="secondary" href={exportDownloadUrl} download={CSV_CONTENT_TYPE}>Export</Button>
                            </ButtonGroup>
                        </Stack>
                    </div>
                    <Outlet context={{ placement, error, loading, axiosFetch, getPlacement }} />
                    <Modal show={showImport}>
                        <Modal.Header closeButton onHide={() => setShowImport(false)}>
                            <Modal.Title>Import</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <Import placement={placement} />
                        </Modal.Body>
                    </Modal>
                </article>
            }
        </>
    );
}