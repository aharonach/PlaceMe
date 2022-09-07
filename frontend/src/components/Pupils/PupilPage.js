import {useParams, useNavigate, Outlet} from "react-router-dom";
import useFetchRecord from "../../hooks/useFetchRecord";
import Loading from "../Loading";
import {Alert, Button, ButtonGroup} from "react-bootstrap";
import {LinkContainer} from "react-router-bootstrap";

export default function PupilPage() {
    let { pupilId } = useParams();
    const [pupil, error, loading, axiosFetch] = useFetchRecord({
        fetchUrl: `/pupils/${pupilId}`,
        displayFields: ['firstName', 'lastName']
    });
    let navigate = useNavigate();

    const handleDelete = () => {
        axiosFetch({
            method: 'delete',
            url: `/pupils/${pupilId}`,
        }).then(() => navigate('/pupils', { replace: true }));
    }

    return (
        <>
            <Loading show={loading} />
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && pupil &&
                <article className="pupil">
                    <h1>{pupil.firstName} {pupil.lastName}</h1>
                    <ButtonGroup>
                        <LinkContainer to={`/pupils/${pupil.id}/edit`}><Button>Edit</Button></LinkContainer>
                        <LinkContainer to={`/pupils/${pupil.id}/groups`}><Button>Attribute Values</Button></LinkContainer>
                        <Button variant="danger" onClick={handleDelete}>Delete Pupil</Button>
                    </ButtonGroup>
                    <Outlet context={{ pupil, error, loading, axiosFetch }} />
                </article>
            }
        </>
    )
}
