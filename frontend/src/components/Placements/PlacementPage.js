import {Outlet, useNavigate, useParams} from "react-router-dom";
import Loading from "../Loading";
import {Alert, Button, ButtonGroup} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";
import useFetchRecord from "../../hooks/useFetchRecord";

export default function PlacementPage({edit=false}){
    let { placementId } = useParams();
    const [placement, error, loading, axiosFetch] = useFetchRecord({
        fetchUrl: `/placements/${placementId}`,
        displayFields: [ 'name' ],
    });

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
                    <h1>{placement.name}</h1>
                    <ButtonGroup>
                        <LinkContainer to={`/placements/${placement.id}/edit`}><Button>Edit Placement</Button></LinkContainer>
                        <LinkContainer to={`/placements/${placement.id}/results`}><Button>Show All Optional Results</Button></LinkContainer>
                        <Button variant="danger" onClick={handleDelete}>Delete Placement</Button>
                    </ButtonGroup>
                    <Outlet context={{ placement, error, loading, axiosFetch }} />
                </article>
            }
        </>
    );
}