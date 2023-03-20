import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px

import os
import sys
# Append parent directory to import path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import config
pio.kaleido.scope.mathjax = None

windowType = ['Sum', 'Average', 'Max',
              'Median']


fig = go.Figure()

fig.add_trace(go.Bar(name="Central", x=windowType,
                     y=[2151978.274, 2171516.619, 2426088.523, 516726.73]
                     , legendrank=1, width=0.4
                     , marker_color=config.central))
fig.add_trace(go.Bar(name="Deco", x=windowType,
                     y=[4303603.042, 4429281.64, 4626237.657, 622017.147]
                     , legendrank=4, width=0.4
                     , marker_color=config.desis))

#legend
fig.update_layout(
    legend=dict(
        orientation="h",
        yanchor="top",
        y=1.2,
        xanchor="left",
        x=0.15,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=30,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="events/sec",
        titlefont=dict(size=35),
        ticktext=["0", "1M", "2M", "3M", "4M", "5M"],
        tickvals=[0, 1000000, 2000000, 3000000, 4000000, 5000000],
        tickmode="array",
        range=[0, 5100000],
        ticks="inside",
        ticklen=20,
        tickwidth=5,
    ),
    xaxis=dict(
        # title_text="aggregation functions                 window types",
        # titlefont=dict(size=35),
        ticktext=['Sum', 'Average', 'Max',
              'Median'],
        # tickvals=[1, 2, 3, 4],
        tickmode="array",
        # range=[0, 6],
    ),
)

# size & color
fig.update_layout(
    autosize=False,
    width=660,
    height=440,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)',
    margin=dict(
        l=5,
        r=5,
        b=5,
        t=5,
        pad=0
    ),
)
fig.update_layout(barmode='group', bargap=0.2, bargroupgap=0.0)

fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=25))
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=35))
fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')



fig.show()
if not os.path.exists("E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\single"):
    os.mkdir("E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\single")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\single\singleFW.pdf")
pio.write_image(fig, "E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\single\singleFW.svg")