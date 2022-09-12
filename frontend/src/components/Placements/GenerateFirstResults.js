import {LinkContainer} from "react-router-bootstrap";
import {Alert, Button} from "react-bootstrap";
import HeroAddRecord from "../General/HeroAddRecord";
import Loading from "../Loading";
import {useNavigate, useOutletContext} from "react-router-dom";
import useAxios from "../../hooks/useAxios";

export default function GenerateFirstResults({ hasResults }) {
    const { placement, getPlacement } = useOutletContext();
    // eslint-disable-next-line no-unused-vars
    const [results, error, loading, axiosFetch] = useAxios();
    const navigate = useNavigate();

    if ( hasResults ) {
        return <HeroAddRecord
            title={<h2>"{placement.name}" has {placement.numberOfResults} results</h2>}
            message={<p>No selected result, go to the results page to select one.</p>}
            button={<LinkContainer to={`/placements/${placement.id}/results`}><Button size="lg" variant="success">View the results</Button></LinkContainer>}
        />
    }

    const generateFirstResults = () => {
        axiosFetch({
            method: 'post',
            url: `/placements/${placement.id}/results/generate?amountOfResults=3`,
            data: { name: "Initial Result" }
        }).then( res => {
            if ( res ) {
                getPlacement();
                navigate(`/placements/${placement.id}/results`, { replace: true });
            }
        });
    }

    return <HeroAddRecord
        title={<h2>No results yet!</h2>}
        message={<>
            {error && <Alert variant="danger">{error}</Alert> }
            <p>Click on start to create the first 3 results</p>
        </>}
        button={<Button size="lg" onClick={generateFirstResults}><Loading show={loading} block={false} /> Start Now</Button>}
    />;
}